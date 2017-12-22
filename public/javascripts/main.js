$(document).ready(function(){
    $(".owl-carousel").owlCarousel({
        loop:true,
        margin:10,
        // nav:true,
        items: 1,
        autoplay: true
        // autoWidth:true
        // responsive:{
        //     0:{
        //         items:1
        //     },
        //     600:{
        //         items:1
        //     },
        //     1000:{
        //         items:1
        //     }
        // }
    });


    $('#menu1').metisMenu({ toggle: false });


    $('#langs').change(function () {

        window.location.href = "/lang/" + $(this).val()
    })

});
