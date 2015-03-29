var wd = require("miga.tiwifidirect");

wd.create({});

wd.discover({
    success:onSuccessDiscover
});

function onSuccessDiscover(e){
    wd.connect({
        success:onSuccessConnect,
        error: onErrorConnect
    });
}


function onErrorConnect(){
    $.lbl.text = "no devices";
}
function onSuccessConnect(e){
    $.lbl.text = e.device;
}

function onClickBtn(e){
    $.lbl.text="";
    wd.connect({
        success:onSuccessConnect,
        error: onErrorConnect
    });
}

$.btn.addEventListener("click",onClickBtn);
$.index.open();
