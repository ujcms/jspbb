!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(x){"use strict";function e(e){for(var t={},n=e.split(" "),r=0;r<n.length;++r)t[n[r]]=!0;return t}x.defineMode("d",function(e,t){var o,a=e.indentUnit,u=t.statementIndentUnit||a,l=t.keywords||{},s=t.builtin||{},c=t.blockKeywords||{},f=t.atoms||{},d=t.hooks||{},m=t.multiLineStrings,p=/[+\-*&%=<>!?|\/]/;function y(e,t){var n=e.next();if(d[n]){var r=d[n](e,t);if(!1!==r)return r}if('"'==n||"'"==n||"`"==n)return t.tokenize=function(o){return function(e,t){for(var n,r=!1,i=!1;null!=(n=e.next());){if(n==o&&!r){i=!0;break}r=!r&&"\\"==n}return!i&&(r||m)||(t.tokenize=null),"string"}}(n),t.tokenize(e,t);if(/[\[\]{}\(\),;\:\.]/.test(n))return o=n,null;if(/\d/.test(n))return e.eatWhile(/[\w\.]/),"number";if("/"==n){if(e.eat("+"))return(t.tokenize=h)(e,t);if(e.eat("*"))return(t.tokenize=b)(e,t);if(e.eat("/"))return e.skipToEnd(),"comment"}if(p.test(n))return e.eatWhile(p),"operator";e.eatWhile(/[\w\$_\xa1-\uffff]/);var i=e.current();return l.propertyIsEnumerable(i)?(c.propertyIsEnumerable(i)&&(o="newstatement"),"keyword"):s.propertyIsEnumerable(i)?(c.propertyIsEnumerable(i)&&(o="newstatement"),"builtin"):f.propertyIsEnumerable(i)?"atom":"variable"}function b(e,t){for(var n,r=!1;n=e.next();){if("/"==n&&r){t.tokenize=null;break}r="*"==n}return"comment"}function h(e,t){for(var n,r=!1;n=e.next();){if("/"==n&&r){t.tokenize=null;break}r="+"==n}return"comment"}function i(e,t,n,r,i){this.indented=e,this.column=t,this.type=n,this.align=r,this.prev=i}function k(e,t,n){var r=e.indented;return e.context&&"statement"==e.context.type&&(r=e.context.indented),e.context=new i(r,t,n,null,e.context)}function v(e){var t=e.context.type;return")"!=t&&"]"!=t&&"}"!=t||(e.indented=e.context.indented),e.context=e.context.prev}return{startState:function(e){return{tokenize:null,context:new i((e||0)-a,0,"top",!1),indented:0,startOfLine:!0}},token:function(e,t){var n=t.context;if(e.sol()&&(null==n.align&&(n.align=!1),t.indented=e.indentation(),t.startOfLine=!0),e.eatSpace())return null;o=null;var r=(t.tokenize||y)(e,t);if("comment"==r||"meta"==r)return r;if(null==n.align&&(n.align=!0),";"!=o&&":"!=o&&","!=o||"statement"!=n.type)if("{"==o)k(t,e.column(),"}");else if("["==o)k(t,e.column(),"]");else if("("==o)k(t,e.column(),")");else if("}"==o){for(;"statement"==n.type;)n=v(t);for("}"==n.type&&(n=v(t));"statement"==n.type;)n=v(t)}else o==n.type?v(t):(("}"==n.type||"top"==n.type)&&";"!=o||"statement"==n.type&&"newstatement"==o)&&k(t,e.column(),"statement");else v(t);return t.startOfLine=!1,r},indent:function(e,t){if(e.tokenize!=y&&null!=e.tokenize)return x.Pass;var n=e.context,r=t&&t.charAt(0);"statement"==n.type&&"}"==r&&(n=n.prev);var i=r==n.type;return"statement"==n.type?n.indented+("{"==r?0:u):n.align?n.column+(i?0:1):n.indented+(i?0:a)},electricChars:"{}",blockCommentStart:"/*",blockCommentEnd:"*/",blockCommentContinue:" * ",lineComment:"//",fold:"brace"}});var t="body catch class do else enum for foreach foreach_reverse if in interface mixin out scope struct switch try union unittest version while with";x.defineMIME("text/x-d",{name:"d",keywords:e("abstract alias align asm assert auto break case cast cdouble cent cfloat const continue debug default delegate delete deprecated export extern final finally function goto immutable import inout invariant is lazy macro module new nothrow override package pragma private protected public pure ref return shared short static super synchronized template this throw typedef typeid typeof volatile __FILE__ __LINE__ __gshared __traits __vector __parameters "+t),blockKeywords:e(t),builtin:e("bool byte char creal dchar double float idouble ifloat int ireal long real short ubyte ucent uint ulong ushort wchar wstring void size_t sizediff_t"),atoms:e("exit failure success true false null"),hooks:{"@":function(e,t){return e.eatWhile(/[\w\$_]/),"meta"}}})});