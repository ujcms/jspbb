!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("rpm-changes",function(){var r=/^-+$/,t=/^(Mon|Tue|Wed|Thu|Fri|Sat|Sun) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)  ?\d{1,2} \d{2}:\d{2}(:\d{2})? [A-Z]{3,4} \d{4} - /,n=/^[\w+.-]+@[\w.-]+/;return{token:function(e){if(e.sol()){if(e.match(r))return"tag";if(e.match(t))return"tag"}return e.match(n)?"string":(e.next(),null)}}}),e.defineMIME("text/x-rpm-changes","rpm-changes"),e.defineMode("rpm-spec",function(){var t=/^(i386|i586|i686|x86_64|ppc64le|ppc64|ppc|ia64|s390x|s390|sparc64|sparcv9|sparc|noarch|alphaev6|alpha|hppa|mipsel)/,n=/^[a-zA-Z0-9()]+:/,o=/^%(debug_package|package|description|prep|build|install|files|clean|changelog|preinstall|preun|postinstall|postun|pretrans|posttrans|pre|post|triggerin|triggerun|verifyscript|check|triggerpostun|triggerprein|trigger)/,a=/^%(ifnarch|ifarch|if)/,i=/^%(else|endif)/,c=/^(\!|\?|\<\=|\<|\>\=|\>|\=\=|\&\&|\|\|)/;return{startState:function(){return{controlFlow:!1,macroParameters:!1,section:!1}},token:function(e,r){if("#"==e.peek())return e.skipToEnd(),"comment";if(e.sol()){if(e.match(n))return"header";if(e.match(o))return"atom"}if(e.match(/^\$\w+/))return"def";if(e.match(/^\$\{\w+\}/))return"def";if(e.match(i))return"keyword";if(e.match(a))return r.controlFlow=!0,"keyword";if(r.controlFlow){if(e.match(c))return"operator";if(e.match(/^(\d+)/))return"number";e.eol()&&(r.controlFlow=!1)}if(e.match(t))return e.eol()&&(r.controlFlow=!1),"number";if(e.match(/^%[\w]+/))return e.match(/^\(/)&&(r.macroParameters=!0),"keyword";if(r.macroParameters){if(e.match(/^\d+/))return"number";if(e.match(/^\)/))return r.macroParameters=!1,"keyword"}return e.match(/^%\{\??[\w \-\:\!]+\}/)?(e.eol()&&(r.controlFlow=!1),"def"):(e.next(),null)}}}),e.defineMIME("text/x-rpm-spec","rpm-spec")});