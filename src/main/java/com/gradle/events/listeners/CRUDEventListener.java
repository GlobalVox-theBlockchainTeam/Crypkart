package com.gradle.events.listeners;

import com.gradle.entity.base.BaseModel;
import org.hibernate.cfg.beanvalidation.GroupsPerOperation;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;


/**
 * This event listener is for hibernate session manager.
 * We are using both entity manager from JPA and Session manager from Hibernate
 * JAP provides preDelete preUpdate annotations but hibernat do not
 * so in case we use session manager somewhere we will be able to use this database triggers from here
 */
@Component
public class CRUDEventListener implements PostCommitInsertEventListener, PostCommitUpdateEventListener, PostCommitDeleteEventListener,
        PreDeleteEventListener, PreUpdateEventListener {
    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        // We must need to return true otherwise we would not be able to receive events
        return true;
    }

    @Override
    public void onPostDeleteCommitFailed(PostDeleteEvent event) {
        // Here we can do something useful, may be log or notify some external system

    }

    @Override
    public void onPostUpdateCommitFailed(PostUpdateEvent event) {
        // Here we can do something useful, may be log or notify some external system

    }

    @Override
    public void onPostInsertCommitFailed(PostInsertEvent event) {
        // Here we can do something useful, may be log or notify some external system

    }

    @Override
    public void onPostInsert(PostInsertEvent event) {

    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {

    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {

    }

    /**
     * If using session manager to delete record
     * this event will be triggered here for all the entities
     * If in child class you have not created following methods
     * it will call BaseModels methods
     * @see BaseModel -> PreInsert or PreUpdate or PreDelete
     * @param event
     * @return
     */
    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        BaseModel base = (BaseModel) event.getEntity();
        base.preDelete();
        return false;
    }


    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        BaseModel base = (BaseModel) event.getEntity();
        base.preUpdate();
        return false;
    }
}