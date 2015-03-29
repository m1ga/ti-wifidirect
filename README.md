Titanium module to draw graphs on android

Binary inside android/dist/ folder

![ScreenShot](https://raw.github.com/m1ga/tigraph/master/android/example/demo.jpg)


Example:

```
var win = Ti.UI.createWindow({
    backgroundColor : "#000"
});

var g = require("miga.tigraph");

var points = [[0, 0], [10, 5], [20, 25], [30, 5]];
var blob = g.drawLines({
    height : 200, maxY : 50, text : true, textColor : "#444444", circles : true, circleRadius : 5, circleColor : "#ff0000", backgroundColor : "#ffffff", color : "#000000", lineWidth : 2, points : points
});

var img = Ti.UI.createImageView({
    image : blob, top : 0
});
win.add(img);

win.open();


```

