!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(a){"use strict";function t(e){e.state.rulerDiv.textContent="";var r=e.getOption("rulers"),t=e.defaultCharWidth(),l=e.charCoords(a.Pos(e.firstLine(),0),"div").left;e.state.rulerDiv.style.minHeight=e.display.scroller.offsetHeight+30+"px";for(var i=0;i<r.length;i++){var o=document.createElement("div");o.className="CodeMirror-ruler";var s,n=r[i];"number"==typeof n?s=n:(s=n.column,n.className&&(o.className+=" "+n.className),n.color&&(o.style.borderColor=n.color),n.lineStyle&&(o.style.borderLeftStyle=n.lineStyle),n.width&&(o.style.borderLeftWidth=n.width)),o.style.left=l+s*t+"px",e.state.rulerDiv.appendChild(o)}}a.defineOption("rulers",!1,function(e,r){e.state.rulerDiv&&(e.state.rulerDiv.parentElement.removeChild(e.state.rulerDiv),e.state.rulerDiv=null,e.off("refresh",t)),r&&r.length&&(e.state.rulerDiv=e.display.lineSpace.parentElement.insertBefore(document.createElement("div"),e.display.lineSpace),e.state.rulerDiv.className="CodeMirror-rulers",t(e),e.on("refresh",t))})});