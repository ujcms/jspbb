!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";function i(e){var t=e.match(/^\s*\S/);return e.skipToEnd(),t?"error":null}e.defineMode("asciiarmor",function(){return{token:function(e,t){var r;if("top"==t.state)return e.sol()&&(r=e.match(/^-----BEGIN (.*)?-----\s*$/))?(t.state="headers",t.type=r[1],"tag"):i(e);if("headers"!=t.state)return"header"==t.state?(e.skipToEnd(),t.state="headers","string"):"body"==t.state?e.sol()&&(r=e.match(/^-----END (.*)?-----\s*$/))?r[1]!=t.type?"error":(t.state="end","tag"):e.eatWhile(/[A-Za-z0-9+\/=]/)?null:(e.next(),"error"):"end"==t.state?i(e):void 0;if(e.sol()&&e.match(/^\w+:/))return t.state="header","atom";var a=i(e);return a&&(t.state="body"),a},blankLine:function(e){"headers"==e.state&&(e.state="body")},startState:function(){return{state:"top",type:null}}}}),e.defineMIME("application/pgp","asciiarmor"),e.defineMIME("application/pgp-encrypted","asciiarmor"),e.defineMIME("application/pgp-keys","asciiarmor"),e.defineMIME("application/pgp-signature","asciiarmor")});