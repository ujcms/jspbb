!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("eiffel",function(){function e(e){for(var t={},r=0,n=e.length;r<n;++r)t[e[r]]=!0;return t}var i=e(["note","across","when","variant","until","unique","undefine","then","strip","select","retry","rescue","require","rename","reference","redefine","prefix","once","old","obsolete","loop","local","like","is","inspect","infix","include","if","frozen","from","external","export","ensure","end","elseif","else","do","creation","create","check","alias","agent","separate","invariant","inherit","indexing","feature","expanded","deferred","class","Void","True","Result","Precursor","False","Current","create","attached","detachable","as","and","implies","not","or"]),o=e([":=","and then","and","or","<<",">>"]);function t(e,t){if(e.eatSpace())return null;var r=e.next();return'"'==r||"'"==r?function(e,t,r){return r.tokenize.push(e),e(t,r)}(function(i,o,a){return function(e,t){for(var r,n=!1;null!=(r=e.next());){if(r==i&&(a||!n)){t.tokenize.pop();break}n=!n&&"%"==r}return o}}(r,"string"),e,t):"-"==r&&e.eat("-")?(e.skipToEnd(),"comment"):":"==r&&e.eat("=")?"operator":/[0-9]/.test(r)?(e.eatWhile(/[xXbBCc0-9\.]/),e.eat(/[\?\!]/),"ident"):/[a-zA-Z_0-9]/.test(r)?(e.eatWhile(/[a-zA-Z_0-9]/),e.eat(/[\?\!]/),"ident"):/[=+\-\/*^%<>~]/.test(r)?(e.eatWhile(/[=+\-\/*^%<>~]/),"operator"):null}return{startState:function(){return{tokenize:[t]}},token:function(e,t){var r=t.tokenize[t.tokenize.length-1](e,t);if("ident"==r){var n=e.current();r=i.propertyIsEnumerable(e.current())?"keyword":o.propertyIsEnumerable(e.current())?"operator":/^[A-Z][A-Z_0-9]*$/g.test(n)?"tag":/^0[bB][0-1]+$/g.test(n)?"number":/^0[cC][0-7]+$/g.test(n)?"number":/^0[xX][a-fA-F0-9]+$/g.test(n)?"number":/^([0-9]+\.[0-9]*)|([0-9]*\.[0-9]+)$/g.test(n)?"number":/^[0-9]+$/g.test(n)?"number":"variable"}return r},lineComment:"--"}}),e.defineMIME("text/x-eiffel","eiffel")});