!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror"),require("./runmode")):"function"==typeof define&&define.amd?define(["../../lib/codemirror","./runmode"],e):e(CodeMirror)}(function(d){"use strict";var n=/^(p|li|div|h\\d|pre|blockquote|td)$/;function u(e,o){if(3==e.nodeType)return o.push(e.nodeValue);for(var r=e.firstChild;r;r=r.nextSibling)u(r,o),n.test(e.nodeType)&&o.push("\n")}d.colorize=function(e,o){e=e||document.body.getElementsByTagName("pre");for(var r=0;r<e.length;++r){var n=e[r],t=n.getAttribute("data-lang")||o;if(t){var i=[];u(n,i),n.innerHTML="",d.runMode(i.join(""),t,n),n.className+=" cm-s-default"}}}});