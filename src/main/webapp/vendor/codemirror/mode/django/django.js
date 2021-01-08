!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror"),require("../htmlmixed/htmlmixed"),require("../../addon/mode/overlay")):"function"==typeof define&&define.amd?define(["../../lib/codemirror","../htmlmixed/htmlmixed","../../addon/mode/overlay"],e):e(CodeMirror)}(function(i){"use strict";i.defineMode("django:inner",function(){var i=["block","endblock","for","endfor","true","false","filter","endfilter","loop","none","self","super","if","elif","endif","as","else","import","with","endwith","without","context","ifequal","endifequal","ifnotequal","endifnotequal","extends","include","load","comment","endcomment","empty","url","static","trans","blocktrans","endblocktrans","now","regroup","lorem","ifchanged","endifchanged","firstof","debug","cycle","csrf_token","autoescape","endautoescape","spaceless","endspaceless","ssi","templatetag","verbatim","endverbatim","widthratio"],n=["add","addslashes","capfirst","center","cut","date","default","default_if_none","dictsort","dictsortreversed","divisibleby","escape","escapejs","filesizeformat","first","floatformat","force_escape","get_digit","iriencode","join","last","length","length_is","linebreaks","linebreaksbr","linenumbers","ljust","lower","make_list","phone2numeric","pluralize","pprint","random","removetags","rjust","safe","safeseq","slice","slugify","stringformat","striptags","time","timesince","timeuntil","title","truncatechars","truncatechars_html","truncatewords","truncatewords_html","unordered_list","upper","urlencode","urlize","urlizetrunc","wordcount","wordwrap","yesno"],o=["==","!=","<",">","<=",">="],a=["in","not","or","and"];function l(e,t){if(e.match("{{"))return t.tokenize=r,"tag";if(e.match("{%"))return t.tokenize=c,"tag";if(e.match("{#"))return t.tokenize=m,"comment";for(;null!=e.next()&&!e.match(/\{[{%#]/,!1););return null}function u(r,i){return function(e,t){!t.escapeNext&&e.eat(r)?t.tokenize=i:(t.escapeNext&&(t.escapeNext=!1),"\\"==e.next()&&(t.escapeNext=!0));return"string"}}function r(e,t){if(t.waitDot){if(t.waitDot=!1,"."!=e.peek())return"null";if(e.match(/\.\W+/))return"error";if(e.eat("."))return t.waitProperty=!0,"null";throw Error("Unexpected error while waiting for property.")}if(t.waitPipe){if(t.waitPipe=!1,"|"!=e.peek())return"null";if(e.match(/\.\W+/))return"error";if(e.eat("|"))return t.waitFilter=!0,"null";throw Error("Unexpected error while waiting for filter.")}return t.waitProperty&&(t.waitProperty=!1,e.match(/\b(\w+)\b/))?(t.waitDot=!0,t.waitPipe=!0,"property"):t.waitFilter&&(t.waitFilter=!1,e.match(n))?"variable-2":e.eatSpace()?(t.waitProperty=!1,"null"):e.match(/\b\d+(\.\d+)?\b/)?"number":e.match("'")?(t.tokenize=u("'",t.tokenize),"string"):e.match('"')?(t.tokenize=u('"',t.tokenize),"string"):e.match(/\b(\w+)\b/)&&!t.foundVariable?(t.waitDot=!0,t.waitPipe=!0,"variable"):e.match("}}")?(t.waitProperty=null,t.waitFilter=null,t.waitDot=null,t.waitPipe=null,t.tokenize=l,"tag"):(e.next(),"null")}function c(e,t){if(t.waitDot){if(t.waitDot=!1,"."!=e.peek())return"null";if(e.match(/\.\W+/))return"error";if(e.eat("."))return t.waitProperty=!0,"null";throw Error("Unexpected error while waiting for property.")}if(t.waitPipe){if(t.waitPipe=!1,"|"!=e.peek())return"null";if(e.match(/\.\W+/))return"error";if(e.eat("|"))return t.waitFilter=!0,"null";throw Error("Unexpected error while waiting for filter.")}if(t.waitProperty&&(t.waitProperty=!1,e.match(/\b(\w+)\b/)))return t.waitDot=!0,t.waitPipe=!0,"property";if(t.waitFilter&&(t.waitFilter=!1,e.match(n)))return"variable-2";if(e.eatSpace())return t.waitProperty=!1,"null";if(e.match(/\b\d+(\.\d+)?\b/))return"number";if(e.match("'"))return t.tokenize=u("'",t.tokenize),"string";if(e.match('"'))return t.tokenize=u('"',t.tokenize),"string";if(e.match(o))return"operator";if(e.match(a))return"keyword";var r=e.match(i);return r?("comment"==r[0]&&(t.blockCommentTag=!0),"keyword"):e.match(/\b(\w+)\b/)?(t.waitDot=!0,t.waitPipe=!0,"variable"):e.match("%}")?(t.waitProperty=null,t.waitFilter=null,t.waitDot=null,t.waitPipe=null,t.blockCommentTag?(t.blockCommentTag=!1,t.tokenize=f):t.tokenize=l,"tag"):(e.next(),"null")}function m(e,t){return e.match(/^.*?#\}/)?t.tokenize=l:e.skipToEnd(),"comment"}function f(e,t){return e.match(/\{%\s*endcomment\s*%\}/,!1)?(t.tokenize=c,e.match("{%"),"tag"):(e.next(),"comment")}return i=new RegExp("^\\b("+i.join("|")+")\\b"),n=new RegExp("^\\b("+n.join("|")+")\\b"),o=new RegExp("^\\b("+o.join("|")+")\\b"),a=new RegExp("^\\b("+a.join("|")+")\\b"),{startState:function(){return{tokenize:l}},token:function(e,t){return t.tokenize(e,t)},blockCommentStart:"{% comment %}",blockCommentEnd:"{% endcomment %}"}}),i.defineMode("django",function(e){var t=i.getMode(e,"text/html"),r=i.getMode(e,"django:inner");return i.overlayMode(t,r)}),i.defineMIME("text/x-django","django")});