!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("fcl",function(e){var o=e.indentUnit,i={term:!0,method:!0,accu:!0,rule:!0,then:!0,is:!0,and:!0,or:!0,if:!0,default:!0},u={var_input:!0,var_output:!0,fuzzify:!0,defuzzify:!0,function_block:!0,ruleblock:!0},a={end_ruleblock:!0,end_defuzzify:!0,end_function_block:!0,end_fuzzify:!0,end_var:!0},c={true:!0,false:!0,nan:!0,real:!0,min:!0,max:!0,cog:!0,cogs:!0},f=/[+\-*&^%:=<>!|\/]/;function l(e,n){var t=e.next();if(/[\d\.]/.test(t))return"."==t?e.match(/^[0-9]+([eE][\-+]?[0-9]+)?/):"0"==t?e.match(/^[xX][0-9a-fA-F]+/)||e.match(/^0[0-7]+/):e.match(/^[0-9]*\.?[0-9]*([eE][\-+]?[0-9]+)?/),"number";if("/"==t||"("==t){if(e.eat("*"))return(n.tokenize=d)(e,n);if(e.eat("/"))return e.skipToEnd(),"comment"}if(f.test(t))return e.eatWhile(f),"operator";e.eatWhile(/[\w\$_\xa1-\uffff]/);var r=e.current().toLowerCase();return i.propertyIsEnumerable(r)||u.propertyIsEnumerable(r)||a.propertyIsEnumerable(r)?"keyword":c.propertyIsEnumerable(r)?"atom":"variable"}function d(e,n){for(var t,r=!1;t=e.next();){if(("/"==t||")"==t)&&r){n.tokenize=l;break}r="*"==t}return"comment"}function m(e,n,t,r,o){this.indented=e,this.column=n,this.type=t,this.align=r,this.prev=o}return{startState:function(e){return{tokenize:null,context:new m((e||0)-o,0,"top",!1),indented:0,startOfLine:!0}},token:function(e,n){var t=n.context;if(e.sol()&&(null==t.align&&(t.align=!1),n.indented=e.indentation(),n.startOfLine=!0),e.eatSpace())return null;var r=(n.tokenize||l)(e,n);if("comment"==r)return r;null==t.align&&(t.align=!0);var o=e.current().toLowerCase();return u.propertyIsEnumerable(o)?function(e,n,t){e.context=new m(e.indented,n,t,null,e.context)}(n,e.column(),"end_block"):a.propertyIsEnumerable(o)&&function(e){if(e.context.prev)"end_block"==e.context.type&&(e.indented=e.context.indented),e.context=e.context.prev}(n),n.startOfLine=!1,r},indent:function(e,n){if(e.tokenize!=l&&null!=e.tokenize)return 0;var t=e.context,r=a.propertyIsEnumerable(n);return t.align?t.column+(r?0:1):t.indented+(r?0:o)},electricChars:"ryk",fold:"brace",blockCommentStart:"(*",blockCommentEnd:"*)",lineComment:"//"}}),e.defineMIME("text/x-fcl","fcl")});