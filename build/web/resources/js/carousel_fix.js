/*The the setIntervalId here is a global parameter*/
var idInterval = 0;
function startCarousel() {
   /*The interval in milisecond coud be passed as a parameter, here it is fixed*/
   carouselNice.cfg.autoPlayInterval = 7000;
   idInterval = setInterval(function() {
      carouselNice.next();
   }, carouselNice.cfg.autoPlayInterval);
}

$(document).ready(function() {
   startCarousel();
   
   $('#featuredUsersCarousel').hover(function() {
      clearInterval(idInterval);
   }, function() {
      startCarousel();
   });
   
});