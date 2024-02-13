console.log("Hello from JS")
const toggleSideBar = () => {

    if($(".sidebar").is(":visible")){
        //if sidebar is visible, close it
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }
    else{
        //if sidebar is close, open it
        $(".sidebar").css("display","block");
        $(".content").css("margin-left", "20%");
    }

};