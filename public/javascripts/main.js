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
    $("[data-fancybox]").fancybox({
        loop: true,
        buttons : [
             'slideShow',
            'fullScreen',
            'thumbs',
            'share',
            'download',
            'zoom',
            'close'
        ],
    });

    $('a[data-fancybox] img.thumb').hover(function (a) {
        $('a[data-fancybox] img.thumb').removeClass("border-primary")
        $('a[data-fancybox] img.thumb').addClass("border-light")
        $(this).removeClass("border-light")
        $(this).addClass("border-primary")

        $('a.proxy img.main').attr('src', $(this).attr('src'))


    },function () {
      //  $(this).removeClass("border border-primary")
    })
    $('a.proxy').click(function () {
        $('a[data-fancybox] img.thumb.border-primary').trigger('click')
        return false
    })




    $(window).scroll(function () {
        if ($(this).scrollTop() > 50) {
            $('#back-to-top').fadeIn();
        } else {
            $('#back-to-top').fadeOut();
        }
    });
    // scroll body to 0px on click
    $('#back-to-top').click(function () {
        $('#back-to-top').tooltip('hide');
        $('body,html').animate({
            scrollTop: 0
        }, 800);
        return false;
    });

    $('#back-to-top').tooltip('show');
});
