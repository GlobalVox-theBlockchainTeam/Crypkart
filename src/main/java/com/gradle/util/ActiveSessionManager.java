/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;

import com.gradle.entity.msg.OutputMessage;
import com.gradle.entity.user.User;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.websocket.WebsocketHelper;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.*;

/**
 * Data store class to store active user sessions. Stores users
 * as the string username. Allows thread safe access to this store.
 * <p>
 * Implement {@link ActiveUserChangeListener} interface to listen
 * change in data in the store.
 * Calling {@link ActiveUserChangeListener#notifyActiveUserChange()}
 * will be done asynchronously by a thread pool with a internal queue.
 *
 * @author Yasitha Thilakaratne
 */
@Component
public class ActiveSessionManager {

    @Autowired
    private UserService userService;

    @Autowired
    private WebsocketHelper websocketHelper;

    @Autowired
    private LocaleHelper localeHelper;



    private final Map<String, Object> map;

    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();

    private List<ActiveUserChangeListener> listeners;

    private ThreadPoolExecutor notifyPool;

    private ActiveSessionManager() {
        map = new ConcurrentHashMap<>();
        listeners = new CopyOnWriteArrayList<>();
        notifyPool = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
    }

    /**
     * Adds new username to the data set.
     *
     * @param username - to be added
     */
    public void add(String username) {
        map.put(username, PRESENT);
        notifyListeners();
        Object[] params = new Object[1];
        params[0] = username;
        String query = "from User where username=?";
        User user = userService.first(query, params);
        user.setLastLoginAt(user.getCurrentLoginAt());
        user.setCurrentLoginAt(new LocalDateTime());
        userService.saveOrUpdate(user);
        if (user.getParent() != null) {
            OutputMessage outputMessage =
                    new OutputMessage(user.getParent().getUsername(), user.getUsername() + " Just logged in", new LocalDateTime().toString(), false);
            websocketHelper.sendWebNotification(user.getParent(), outputMessage);
        }
        if (user.getLastLoginAt()!=null){
            localeHelper.setShowLastLogin(true);
        }


    }

    /**
     * Removes username from the store.
     *
     * @param username - to be removed
     */
    public void remove(String username) {
        map.remove(username);
        notifyListeners();
    }

    /**
     * Clears all data
     */
    public void clear() {
        synchronized (map) {
            map.clear();
            notifyListeners();
        }
    }

    /**
     * Get all active user data
     *
     * @return - Set of active username.
     */
    public Set<String> getAll() {
        return map.keySet();
    }

    /**
     * Get a set of all active usernames except username passed as
     * the argument.
     *
     * @param currentUsername - current username
     * @return - set of usernames except passed username
     */
    public Set<String> getAllExceptCurrentUser(String currentUsername) {
        Set<String> users = new HashSet<>(map.keySet());
        users.remove(currentUsername);
        return users;
    }

    /**
     * Add all values in the collection
     *
     * @param usernames - Collection of usernames
     */
    public void addAll(Collection<String> usernames) {
        usernames.forEach(s -> map.put(s, PRESENT));
        notifyListeners();
    }

    /**
     * Register {@link ActiveUserChangeListener} instance to get notified
     * when active users changed. Starts sending notification calls
     * asynchronously after registered.
     *
     * @param listener - instance of class that implements
     *                 {@link ActiveUserChangeListener}
     *                 interface
     */
    public void registerListener(ActiveUserChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregister {@link ActiveUserChangeListener} instance to stop
     * receiving notifying method calls.
     *
     * @param listener - instance of class that implements
     *                 {@link ActiveUserChangeListener}
     *                 interface
     */
    public void removeListener(ActiveUserChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        notifyPool.submit(() -> listeners.forEach(ActiveUserChangeListener::notifyActiveUserChange));
    }

    /**
     * Implement this interface to get register as an Observer for
     * ActiveSessionManager class. Pass instant through
     * {@link ActiveSessionManager#registerListener(ActiveUserChangeListener)}
     * method to get notified when active user sessions are changed.
     * <p>
     * ${@link ActiveUserChangeListener#notifyActiveUserChange()} will be
     * called when internal object status is changed.
     */
    public interface ActiveUserChangeListener {
        /**
         * This method will get called when Observable's internal state
         * is changed.
         */
        void notifyActiveUserChange();
    }
}
