!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("velocity",function(){function e(e){for(var t={},n=e.split(" "),r=0;r<n.length;++r)t[n[r]]=!0;return t}var a=e("#end #else #break #stop #[[ #]] #{end} #{else} #{break} #{stop}"),o=e("#if #elseif #foreach #set #include #parse #macro #define #evaluate #{if} #{elseif} #{foreach} #{set} #{include} #{parse} #{macro} #{define} #{evaluate}"),s=e("$foreach.count $foreach.hasNext $foreach.first $foreach.last $foreach.topmost $foreach.parent.count $foreach.parent.hasNext $foreach.parent.first $foreach.parent.last $foreach.parent $velocityCount $!bodyContent $bodyContent"),l=/[+\-*&%=<>!?:\/|]/;function u(e,t,n){return(t.tokenize=n)(e,t)}function f(e,t){var n=t.beforeParams;t.beforeParams=!1;var r=e.next();if("'"==r&&!t.inString&&t.inParams)return t.lastTokenWasBuiltin=!1,u(e,t,c(r));if('"'==r)return t.lastTokenWasBuiltin=!1,t.inString?(t.inString=!1,"string"):t.inParams?u(e,t,c(r)):void 0;if(/[\[\]{}\(\),;\.]/.test(r))return"("==r&&n?t.inParams=!0:")"==r&&(t.inParams=!1,t.lastTokenWasBuiltin=!0),null;if(/\d/.test(r))return t.lastTokenWasBuiltin=!1,e.eatWhile(/[\w\.]/),"number";if("#"==r&&e.eat("*"))return t.lastTokenWasBuiltin=!1,u(e,t,k);if("#"==r&&e.match(/ *\[ *\[/))return t.lastTokenWasBuiltin=!1,u(e,t,m);if("#"==r&&e.eat("#"))return t.lastTokenWasBuiltin=!1,e.skipToEnd(),"comment";if("$"==r)return e.eatWhile(/[\w\d\$_\.{}-]/),s&&s.propertyIsEnumerable(e.current())?"keyword":(t.lastTokenWasBuiltin=!0,t.beforeParams=!0,"builtin");if(l.test(r))return t.lastTokenWasBuiltin=!1,e.eatWhile(l),"operator";e.eatWhile(/[\w\$_{}@]/);var i=e.current();return a&&a.propertyIsEnumerable(i)?"keyword":o&&o.propertyIsEnumerable(i)||e.current().match(/^#@?[a-z0-9_]+ *$/i)&&"("==e.peek()&&(!o||!o.propertyIsEnumerable(i.toLowerCase()))?(t.beforeParams=!0,t.lastTokenWasBuiltin=!1,"keyword"):t.inString?(t.lastTokenWasBuiltin=!1,"string"):e.pos>i.length&&"."==e.string.charAt(e.pos-i.length-1)&&t.lastTokenWasBuiltin?"builtin":(t.lastTokenWasBuiltin=!1,null)}function c(a){return function(e,t){for(var n,r=!1,i=!1;null!=(n=e.next());){if(n==a&&!r){i=!0;break}if('"'==a&&"$"==e.peek()&&!r){i=t.inString=!0;break}r=!r&&"\\"==n}return i&&(t.tokenize=f),"string"}}function k(e,t){for(var n,r=!1;n=e.next();){if("#"==n&&r){t.tokenize=f;break}r="*"==n}return"comment"}function m(e,t){for(var n,r=0;n=e.next();){if("#"==n&&2==r){t.tokenize=f;break}"]"==n?r++:" "!=n&&(r=0)}return"meta"}return{startState:function(){return{tokenize:f,beforeParams:!1,inParams:!1,inString:!1,lastTokenWasBuiltin:!1}},token:function(e,t){return e.eatSpace()?null:t.tokenize(e,t)},blockCommentStart:"#*",blockCommentEnd:"*#",lineComment:"##",fold:"velocity"}}),e.defineMIME("text/velocity","velocity")});