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


    // $('#tree').treeview({
    //     enableLinks: true,
    //     color: "#428bca",
    //     showTags: true,
    //     expandIcon: 'glyphicon glyphicon-chevron-right',
    //     collapseIcon: 'glyphicon glyphicon-chevron-down',
    //
    //     data: getTree()});

});
//
// function getTree() {
//     var tree = [
//         {
//             text: "Каталог",
//             selectable: false,
//             backColor: "#6684ff",
//             color:"ffffff",
//             enableLinks: false
//         },
//         {
//             text: "Parent 1",
//             nodes: [
//                 {
//                     text: "Child 1",
//                     // icon: "glyphicon glyphicon-stop",
//                     // selectedIcon: "glyphicon glyphicon-stop",
//                     color: "#000000",
//                     backColor: "#FFFFFF",
//                     href: "#node-1",
//                     selectable: true,
//                     state: {
//                         checked: true,
//                         // disabled: true,
//                         expanded: true,
//                         selected: true
//                     },
//                     tags: ['available'],
//                     nodes: [
//                         {
//                             text: "Grandchild 1",
//                             href: "/abcdef"
//                         },
//                         {
//                             text: "Grandchild 2"
//                         }
//                     ]
//                 },
//                 {
//                     text: "Child 2"
//                 }
//             ]
//         },
//         {
//             text: "Parent 2"
//         },
//         {
//             text: "Parent 3"
//         },
//         {
//             text: "Parent 4"
//         },
//         {
//             text: "Parent 5"
//         }
//     ];
//     return tree;
// }