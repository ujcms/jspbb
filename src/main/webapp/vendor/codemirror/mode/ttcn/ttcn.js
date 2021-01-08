!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(o){"use strict";function e(e){for(var t={},n=e.split(" "),r=0;r<n.length;++r)t[n[r]]=!0;return t}o.defineMode("ttcn",function(e,t){var i,n=e.indentUnit,o=t.keywords||{},s=t.builtin||{},a=t.timerOps||{},c=t.portOps||{},l=t.configOps||{},p=t.verdictOps||{},u=t.sutOps||{},f=t.functionOps||{},d=t.verdictConsts||{},m=t.booleanConsts||{},b=t.otherConsts||{},h=t.types||{},y=t.visibilityModifiers||{},v=t.templateMatch||{},x=t.multiLineStrings,g=!1!==t.indentStatements,k=/[+\-*&@=<>!\/]/;function O(e,t){var n=e.next();if('"'==n||"'"==n)return t.tokenize=function(s){return function(e,t){for(var n,r=!1,i=!1;null!=(n=e.next());){if(n==s&&!r){var o=e.peek();o&&("b"!=(o=o.toLowerCase())&&"h"!=o&&"o"!=o||e.next()),i=!0;break}r=!r&&"\\"==n}return!i&&(r||x)||(t.tokenize=null),"string"}}(n),t.tokenize(e,t);if(/[\[\]{}\(\),;\\:\?\.]/.test(n))return i=n,"punctuation";if("#"==n)return e.skipToEnd(),"atom preprocessor";if("%"==n)return e.eatWhile(/\b/),"atom ttcn3Macros";if(/\d/.test(n))return e.eatWhile(/[\w\.]/),"number";if("/"==n){if(e.eat("*"))return(t.tokenize=E)(e,t);if(e.eat("/"))return e.skipToEnd(),"comment"}if(k.test(n))return"@"==n&&(e.match("try")||e.match("catch")||e.match("lazy"))?"keyword":(e.eatWhile(k),"operator");e.eatWhile(/[\w\$_\xa1-\uffff]/);var r=e.current();return o.propertyIsEnumerable(r)?"keyword":s.propertyIsEnumerable(r)?"builtin":a.propertyIsEnumerable(r)?"def timerOps":l.propertyIsEnumerable(r)?"def configOps":p.propertyIsEnumerable(r)?"def verdictOps":c.propertyIsEnumerable(r)?"def portOps":u.propertyIsEnumerable(r)?"def sutOps":f.propertyIsEnumerable(r)?"def functionOps":d.propertyIsEnumerable(r)?"string verdictConsts":m.propertyIsEnumerable(r)?"string booleanConsts":b.propertyIsEnumerable(r)?"string otherConsts":h.propertyIsEnumerable(r)?"builtin types":y.propertyIsEnumerable(r)?"builtin visibilityModifiers":v.propertyIsEnumerable(r)?"atom templateMatch":"variable"}function E(e,t){for(var n,r=!1;n=e.next();){if("/"==n&&r){t.tokenize=null;break}r="*"==n}return"comment"}function w(e,t,n,r,i){this.indented=e,this.column=t,this.type=n,this.align=r,this.prev=i}function C(e,t,n){var r=e.indented;return e.context&&"statement"==e.context.type&&(r=e.context.indented),e.context=new w(r,t,n,null,e.context)}function I(e){var t=e.context.type;return")"!=t&&"]"!=t&&"}"!=t||(e.indented=e.context.indented),e.context=e.context.prev}return{startState:function(e){return{tokenize:null,context:new w((e||0)-n,0,"top",!1),indented:0,startOfLine:!0}},token:function(e,t){var n=t.context;if(e.sol()&&(null==n.align&&(n.align=!1),t.indented=e.indentation(),t.startOfLine=!0),e.eatSpace())return null;i=null;var r=(t.tokenize||O)(e,t);if("comment"==r)return r;if(null==n.align&&(n.align=!0),";"!=i&&":"!=i&&","!=i||"statement"!=n.type)if("{"==i)C(t,e.column(),"}");else if("["==i)C(t,e.column(),"]");else if("("==i)C(t,e.column(),")");else if("}"==i){for(;"statement"==n.type;)n=I(t);for("}"==n.type&&(n=I(t));"statement"==n.type;)n=I(t)}else i==n.type?I(t):g&&(("}"==n.type||"top"==n.type)&&";"!=i||"statement"==n.type&&"newstatement"==i)&&C(t,e.column(),"statement");else I(t);return t.startOfLine=!1,r},electricChars:"{}",blockCommentStart:"/*",blockCommentEnd:"*/",lineComment:"//",fold:"brace"}}),function(e,t){"string"==typeof e&&(e=[e]);var n=[];function r(e){if(e)for(var t in e)e.hasOwnProperty(t)&&n.push(t)}r(t.keywords),r(t.builtin),r(t.timerOps),r(t.portOps),n.length&&(t.helperType=e[0],o.registerHelper("hintWords",e[0],n));for(var i=0;i<e.length;++i)o.defineMIME(e[i],t)}(["text/x-ttcn","text/x-ttcn3","text/x-ttcnpp"],{name:"ttcn",keywords:e("activate address alive all alt altstep and and4b any break case component const continue control deactivate display do else encode enumerated except exception execute extends extension external for from function goto group if import in infinity inout interleave label language length log match message mixed mod modifies module modulepar mtc noblock not not4b nowait of on optional or or4b out override param pattern port procedure record recursive rem repeat return runs select self sender set signature system template testcase to type union value valueof var variant while with xor xor4b"),builtin:e("bit2hex bit2int bit2oct bit2str char2int char2oct encvalue decomp decvalue float2int float2str hex2bit hex2int hex2oct hex2str int2bit int2char int2float int2hex int2oct int2str int2unichar isbound ischosen ispresent isvalue lengthof log2str oct2bit oct2char oct2hex oct2int oct2str regexp replace rnd sizeof str2bit str2float str2hex str2int str2oct substr unichar2int unichar2char enum2int"),types:e("anytype bitstring boolean char charstring default float hexstring integer objid octetstring universal verdicttype timer"),timerOps:e("read running start stop timeout"),portOps:e("call catch check clear getcall getreply halt raise receive reply send trigger"),configOps:e("create connect disconnect done kill killed map unmap"),verdictOps:e("getverdict setverdict"),sutOps:e("action"),functionOps:e("apply derefers refers"),verdictConsts:e("error fail inconc none pass"),booleanConsts:e("true false"),otherConsts:e("null NULL omit"),visibilityModifiers:e("private public friend"),templateMatch:e("complement ifpresent subset superset permutation"),multiLineStrings:!0})});