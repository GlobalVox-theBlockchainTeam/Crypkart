/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

(function($){

  $.extend({
    playSound: function(){
      return $(
        '<audio autoplay="autoplay" style="display:none;">'
          + '<source src="' + arguments[0] + '.mp3" />'
          + '<source src="' + arguments[0] + '.ogg" />'
          + '<embed src="' + arguments[0] + '.mp3" hidden="true" autostart="true" loop="false" class="playSound" />'
        + '</audio>'
      ).appendTo('body');
    }
  });

})(jQuery);
