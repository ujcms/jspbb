!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("q",function(e){var i,c=e.indentUnit,s=new RegExp("^("+["abs","acos","aj","aj0","all","and","any","asc","asin","asof","atan","attr","avg","avgs","bin","by","ceiling","cols","cor","cos","count","cov","cross","csv","cut","delete","deltas","desc","dev","differ","distinct","div","do","each","ej","enlist","eval","except","exec","exit","exp","fby","fills","first","fkeys","flip","floor","from","get","getenv","group","gtime","hclose","hcount","hdel","hopen","hsym","iasc","idesc","if","ij","in","insert","inter","inv","key","keys","last","like","list","lj","load","log","lower","lsq","ltime","ltrim","mavg","max","maxs","mcount","md5","mdev","med","meta","min","mins","mmax","mmin","mmu","mod","msum","neg","next","not","null","or","over","parse","peach","pj","plist","prd","prds","prev","prior","rand","rank","ratios","raze","read0","read1","reciprocal","reverse","rload","rotate","rsave","rtrim","save","scan","select","set","setenv","show","signum","sin","sqrt","ss","ssr","string","sublist","sum","sums","sv","system","tables","tan","til","trim","txf","type","uj","ungroup","union","update","upper","upsert","value","var","view","views","vs","wavg","where","where","while","within","wj","wj1","wsum","xasc","xbar","xcol","xcols","xdesc","xexp","xgroup","xkey","xlog","xprev","xrank"].join("|")+")$"),a=/[|/&^!+:\\\-*%$=~#;@><,?_\'\"\[\(\]\)\s{}]/;function l(e,t){var n=e.sol(),o=e.next();if(i=null,n){if("/"==o)return(t.tokenize=d)(e,t);if("\\"==o)return e.eol()||/\s/.test(e.peek())?(e.skipToEnd(),/^\\\s*$/.test(e.current())?(t.tokenize=u)(e):t.tokenize=l,"comment"):(t.tokenize=l,"builtin")}if(/\s/.test(o))return"/"==e.peek()?(e.skipToEnd(),"comment"):"whitespace";if('"'==o)return(t.tokenize=m)(e,t);if("`"==o)return e.eatWhile(/[A-Za-z\d_:\/.]/),"symbol";if("."==o&&/\d/.test(e.peek())||/\d/.test(o)){var r=null;return e.backUp(1),e.match(/^\d{4}\.\d{2}(m|\.\d{2}([DT](\d{2}(:\d{2}(:\d{2}(\.\d{1,9})?)?)?)?)?)/)||e.match(/^\d+D(\d{2}(:\d{2}(:\d{2}(\.\d{1,9})?)?)?)/)||e.match(/^\d{2}:\d{2}(:\d{2}(\.\d{1,9})?)?/)||e.match(/^\d+[ptuv]{1}/)?r="temporal":(e.match(/^0[NwW]{1}/)||e.match(/^0x[\da-fA-F]*/)||e.match(/^[01]+[b]{1}/)||e.match(/^\d+[chijn]{1}/)||e.match(/-?\d*(\.\d*)?(e[+\-]?\d+)?(e|f)?/))&&(r="number"),!r||(o=e.peek())&&!a.test(o)?(e.next(),"error"):r}return/[A-Za-z]|\./.test(o)?(e.eatWhile(/[A-Za-z._\d]/),s.test(e.current())?"keyword":"variable"):/[|/&^!+:\\\-*%$=~#;@><\.,?_\']/.test(o)?null:/[{}\(\[\]\)]/.test(o)?null:"error"}function d(e,t){return e.skipToEnd(),/\/\s*$/.test(e.current())?(t.tokenize=n)(e,t):t.tokenize=l,"comment"}function n(e,t){var n=e.sol()&&"\\"==e.peek();return e.skipToEnd(),n&&/^\\\s*$/.test(e.current())&&(t.tokenize=l),"comment"}function u(e){return e.skipToEnd(),"comment"}function m(e,t){for(var n,o=!1,r=!1;n=e.next();){if('"'==n&&!o){r=!0;break}o=!o&&"\\"==n}return r&&(t.tokenize=l),"string"}function o(e,t,n){e.context={prev:e.context,indent:e.indent,col:n,type:t}}function r(e){e.indent=e.context.indent,e.context=e.context.prev}return{startState:function(){return{tokenize:l,context:null,indent:0,col:0}},token:function(e,t){e.sol()&&(t.context&&null==t.context.align&&(t.context.align=!1),t.indent=e.indentation());var n=t.tokenize(e,t);if("comment"!=n&&t.context&&null==t.context.align&&"pattern"!=t.context.type&&(t.context.align=!0),"("==i)o(t,")",e.column());else if("["==i)o(t,"]",e.column());else if("{"==i)o(t,"}",e.column());else if(/[\]\}\)]/.test(i)){for(;t.context&&"pattern"==t.context.type;)r(t);t.context&&i==t.context.type&&r(t)}else"."==i&&t.context&&"pattern"==t.context.type?r(t):/atom|string|variable/.test(n)&&t.context&&(/[\}\]]/.test(t.context.type)?o(t,"pattern",e.column()):"pattern"!=t.context.type||t.context.align||(t.context.align=!0,t.context.col=e.column()));return n},indent:function(e,t){var n=t&&t.charAt(0),o=e.context;if(/[\]\}]/.test(n))for(;o&&"pattern"==o.type;)o=o.prev;var r=o&&n==o.type;return o?"pattern"==o.type?o.col:o.align?o.col+(r?0:1):o.indent+(r?0:c):0}}}),e.defineMIME("text/x-q","q")});