!function(t){"object"==typeof exports&&"object"==typeof module?t(require("../../lib/codemirror"),require("../yaml/yaml")):"function"==typeof define&&define.amd?define(["../../lib/codemirror","../yaml/yaml"],t):t(CodeMirror)}(function(o){o.defineMode("yaml-frontmatter",function(t,e){var i=o.getMode(t,"yaml"),a=o.getMode(t,e&&e.base||"gfm");function n(t){return 2==t.state?a:i}return{startState:function(){return{state:0,inner:o.startState(i)}},copyState:function(t){return{state:t.state,inner:o.copyState(n(t),t.inner)}},token:function(t,e){if(0==e.state)return t.match(/---/,!1)?(e.state=1,i.token(t,e.inner)):(e.state=2,e.inner=o.startState(a),a.token(t,e.inner));if(1!=e.state)return a.token(t,e.inner);var n=t.sol()&&t.match(/---/,!1),r=i.token(t,e.inner);return n&&(e.state=2,e.inner=o.startState(a)),r},innerMode:function(t){return{mode:n(t),state:t.inner}},blankLine:function(t){var e=n(t);if(e.blankLine)return e.blankLine(t.inner)}}})});