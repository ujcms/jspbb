!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror"),require("../htmlmixed/htmlmixed"),require("../ruby/ruby")):"function"==typeof define&&define.amd?define(["../../lib/codemirror","../htmlmixed/htmlmixed","../ruby/ruby"],e):e(CodeMirror)}(function(t){"use strict";t.defineMode("haml",function(e){var i=t.getMode(e,{name:"htmlmixed"}),n=t.getMode(e,"ruby");function r(n){return function(e,t){return e.peek()==n&&1==t.rubyState.tokenize.length?(e.next(),t.tokenize=u,"closeAttributeTag"):o(e,t)}}function o(e,t){return e.match("-#")?(e.skipToEnd(),"comment"):n.token(e,t.rubyState)}function u(e,t){var n=e.peek();if("comment"==t.previousToken.style&&t.indented>t.previousToken.indented)return e.skipToEnd(),"commentLine";if(t.startOfLine){if("!"==n&&e.match("!!"))return e.skipToEnd(),"tag";if(e.match(/^%[\w:#\.]+=/))return t.tokenize=o,"hamlTag";if(e.match(/^%[\w:]+/))return"hamlTag";if("/"==n)return e.skipToEnd(),"comment"}if((t.startOfLine||"hamlTag"==t.previousToken.style)&&("#"==n||"."==n))return e.match(/[\w-#\.]*/),"hamlAttribute";if(t.startOfLine&&!e.match("--\x3e",!1)&&("="==n||"-"==n))return t.tokenize=o,t.tokenize(e,t);if("hamlTag"==t.previousToken.style||"closeAttributeTag"==t.previousToken.style||"hamlAttribute"==t.previousToken.style){if("("==n)return t.tokenize=r(")"),t.tokenize(e,t);if("{"==n&&!e.match(/^\{%.*/))return t.tokenize=r("}"),t.tokenize(e,t)}return i.token(e,t.htmlState)}return{startState:function(){return{htmlState:t.startState(i),rubyState:t.startState(n),indented:0,previousToken:{style:null,indented:0},tokenize:u}},copyState:function(e){return{htmlState:t.copyState(i,e.htmlState),rubyState:t.copyState(n,e.rubyState),indented:e.indented,previousToken:e.previousToken,tokenize:e.tokenize}},token:function(e,t){if(e.sol()&&(t.indented=e.indentation(),t.startOfLine=!0),e.eatSpace())return null;var n=t.tokenize(e,t);if(t.startOfLine=!1,n&&"commentLine"!=n&&(t.previousToken={style:n,indented:t.indented}),e.eol()&&t.tokenize==o){e.backUp(1);var i=e.peek();e.next(),i&&","!=i&&(t.tokenize=u)}return"hamlTag"==n?n="tag":"commentLine"==n?n="comment":"hamlAttribute"==n?n="attribute":"closeAttributeTag"==n&&(n=null),n}}},"htmlmixed","ruby"),t.defineMIME("text/x-haml","haml")});