!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("haxe",function(e,n){var o=e.indentUnit;function t(e){return{type:e,style:"keyword"}}var r,a=t("keyword a"),i=t("keyword b"),l=t("keyword c"),u=t("operator"),c={type:"atom",style:"atom"},f={type:"attribute",style:"attribute"},s=t("typedef"),d={if:a,while:a,else:i,do:i,try:i,return:l,break:l,continue:l,new:l,throw:l,var:t("var"),inline:f,static:f,using:t("import"),public:f,private:f,cast:t("cast"),import:t("import"),macro:t("macro"),function:t("function"),catch:t("catch"),untyped:t("untyped"),callback:t("cb"),for:t("for"),switch:t("switch"),case:t("case"),default:t("default"),in:u,never:t("property_access"),trace:t("trace"),class:s,abstract:s,enum:s,interface:s,typedef:s,extends:s,implements:s,dynamic:s,true:c,false:c,null:c},p=/[+\-*&%=<>!?|]/;function m(e,t,n){return(t.tokenize=n)(e,t)}function v(e,t){for(var n,r=!1;null!=(n=e.next());){if(n==t&&!r)return!0;r=!r&&"\\"==n}}function b(e,t,n){return s=e,r=n,t}function y(e,t){var n=e.next();if('"'==n||"'"==n)return m(e,t,function(n){return function(e,t){return v(e,n)&&(t.tokenize=y),b("string","string")}}(n));if(/[\[\]{}\(\),;\:\.]/.test(n))return b(n);if("0"==n&&e.eat(/x/i))return e.eatWhile(/[\da-f]/i),b("number","number");if(/\d/.test(n)||"-"==n&&e.eat(/\d/))return e.match(/^\d*(?:\.\d*(?!\.))?(?:[eE][+\-]?\d+)?/),b("number","number");if(t.reAllowed&&"~"==n&&e.eat(/\//))return v(e,"/"),e.eatWhile(/[gimsu]/),b("regexp","string-2");if("/"==n)return e.eat("*")?m(e,t,x):e.eat("/")?(e.skipToEnd(),b("comment","comment")):(e.eatWhile(p),b("operator",null,e.current()));if("#"==n)return e.skipToEnd(),b("conditional","meta");if("@"==n)return e.eat(/:/),e.eatWhile(/[\w_]/),b("metadata","meta");if(p.test(n))return e.eatWhile(p),b("operator",null,e.current());if(/[A-Z]/.test(n))return e.eatWhile(/[\w_<>]/),b("type","variable-3",r=e.current());e.eatWhile(/[\w_]/);var r=e.current(),a=d.propertyIsEnumerable(r)&&d[r];return a&&t.kwAllowed?b(a.type,a.style,r):b("variable","variable",r)}function x(e,t){for(var n,r=!1;n=e.next();){if("/"==n&&r){t.tokenize=y;break}r="*"==n}return b("comment","comment")}var h={atom:!0,number:!0,variable:!0,string:!0,regexp:!0};function k(e,t,n,r,a,i){this.indented=e,this.column=t,this.type=n,this.prev=a,this.info=i,null!=r&&(this.align=r)}function w(e,t){for(var n=e.localVars;n;n=n.next)if(n.name==t)return!0}function g(e,t){if(/[a-z]/.test(t.charAt(0)))return!1;for(var n=e.importedtypes.length,r=0;r<n;r++)if(e.importedtypes[r]==t)return!0}function V(e){for(var t=A.state,n=t.importedtypes;n;n=n.next)if(n.name==e)return;t.importedtypes={name:e,next:t.importedtypes}}var A={state:null,column:null,marked:null,cc:null};function S(){for(var e=arguments.length-1;0<=e;e--)A.cc.push(arguments[e])}function E(){return S.apply(null,arguments),!0}function W(e,t){for(var n=t;n;n=n.next)if(n.name==e)return!0;return!1}function z(e){var t=A.state;if(t.context){if(A.marked="def",W(e,t.localVars))return;t.localVars={name:e,next:t.localVars}}else if(t.globalVars){if(W(e,t.globalVars))return;t.globalVars={name:e,next:t.globalVars}}}var M={name:"this",next:null};function C(){A.state.context||(A.state.localVars=M),A.state.context={prev:A.state.context,vars:A.state.localVars}}function T(){A.state.localVars=A.state.context.vars,A.state.context=A.state.context.prev}function Z(t,n){function e(){var e=A.state;e.lexical=new k(e.indented,A.stream.column(),t,null,e.lexical,n)}return e.lex=!0,e}function I(){var e=A.state;e.lexical.prev&&(")"==e.lexical.type&&(e.indented=e.lexical.indented),e.lexical=e.lexical.prev)}function O(n){return function e(t){return t==n?E():";"==n?S():E(e)}}function P(e){return"@"==e?E(B):"var"==e?E(Z("vardef"),N,O(";"),I):"keyword a"==e?E(Z("form"),_,P,I):"keyword b"==e?E(Z("form"),P,I):"{"==e?E(Z("}"),C,L,I,T):";"==e?E():"attribute"==e?E(q):"function"==e?E(Y):"for"==e?E(Z("form"),O("("),Z(")"),R,O(")"),I,P,I):"variable"==e?E(Z("stat"),G):"switch"==e?E(Z("form"),_,Z("}","switch"),O("{"),L,I,I):"case"==e?E(_,O(":")):"default"==e?E(O(":")):"catch"==e?E(Z("form"),C,O("("),re,O(")"),P,I,T):"import"==e?E(U,O(";")):"typedef"==e?E($):S(Z("stat"),_,O(";"),I)}function _(e){return h.hasOwnProperty(e)?E(D):"type"==e?E(D):"function"==e?E(Y):"keyword c"==e?E(j):"("==e?E(Z(")"),j,O(")"),I,D):"operator"==e?E(_):"["==e?E(Z("]"),K(j,"]"),I,D):"{"==e?E(Z("}"),K(J,"}"),I,D):E()}function j(e){return e.match(/[;\}\)\],]/)?S():S(_)}function D(e,t){return"operator"==e&&/\+\+|--/.test(t)?E(D):"operator"==e||":"==e?E(_):";"!=e?"("==e?E(Z(")"),K(_,")"),I,D):"."==e?E(H,D):"["==e?E(Z("]"),_,O("]"),I,D):void 0:void 0}function q(e){return"attribute"==e?E(q):"function"==e?E(Y):"var"==e?E(N):void 0}function B(e){return":"==e?E(B):"variable"==e?E(B):"("==e?E(Z(")"),K(F,")"),I,P):void 0}function F(e){if("variable"==e)return E()}function U(e,t){return"variable"==e&&/[A-Z]/.test(t.charAt(0))?(V(t),E()):"variable"==e||"property"==e||"."==e||"*"==t?E(U):void 0}function $(e,t){return"variable"==e&&/[A-Z]/.test(t.charAt(0))?(V(t),E()):"type"==e&&/[A-Z]/.test(t.charAt(0))?E():void 0}function G(e){return":"==e?E(I,P):S(D,O(";"),I)}function H(e){if("variable"==e)return A.marked="property",E()}function J(e){if("variable"==e&&(A.marked="property"),h.hasOwnProperty(e))return E(O(":"),_)}function K(t,n){function r(e){return","==e?E(t,r):e==n?E():E(O(n))}return function(e){return e==n?E():S(t,r)}}function L(e){return"}"==e?E():S(P,L)}function N(e,t){return"variable"==e?(z(t),E(ee,Q)):E()}function Q(e,t){return"="==t?E(_,Q):","==e?E(N):void 0}function R(e,t){return"variable"==e?(z(t),E(X,_)):S()}function X(e,t){if("in"==t)return E()}function Y(e,t){return"variable"==e||"type"==e?(z(t),E(Y)):"new"==t?E(Y):"("==e?E(Z(")"),C,K(re,")"),I,ee,P,T):void 0}function ee(e){if(":"==e)return E(te)}function te(e){return"type"==e?E():"variable"==e?E():"{"==e?E(Z("}"),K(ne,"}"),I):void 0}function ne(e){if("variable"==e)return E(ee)}function re(e,t){if("variable"==e)return z(t),E(ee)}return I.lex=T.lex=!0,{startState:function(e){var t={tokenize:y,reAllowed:!0,kwAllowed:!0,cc:[],lexical:new k((e||0)-o,0,"block",!1),localVars:n.localVars,importedtypes:["Int","Float","String","Void","Std","Bool","Dynamic","Array"],context:n.localVars&&{vars:n.localVars},indented:0};return n.globalVars&&"object"==typeof n.globalVars&&(t.globalVars=n.globalVars),t},token:function(e,t){if(e.sol()&&(t.lexical.hasOwnProperty("align")||(t.lexical.align=!1),t.indented=e.indentation()),e.eatSpace())return null;var n=t.tokenize(e,t);return"comment"==s?n:(t.reAllowed=!("operator"!=s&&"keyword c"!=s&&!s.match(/^[\[{}\(,;:]$/)),t.kwAllowed="."!=s,function(e,t,n,r,a){var i=e.cc;for(A.state=e,A.stream=a,A.marked=null,A.cc=i,e.lexical.hasOwnProperty("align")||(e.lexical.align=!0);;){if((i.length?i.pop():P)(n,r)){for(;i.length&&i[i.length-1].lex;)i.pop()();return A.marked?A.marked:"variable"==n&&w(e,r)?"variable-2":"variable"==n&&g(e,r)?"variable-3":t}}}(t,n,s,r,e))},indent:function(e,t){if(e.tokenize!=y)return 0;var n=t&&t.charAt(0),r=e.lexical;"stat"==r.type&&"}"==n&&(r=r.prev);var a=r.type,i=n==a;return"vardef"==a?r.indented+4:"form"==a&&"{"==n?r.indented:"stat"==a||"form"==a?r.indented+o:"switch"!=r.info||i?r.align?r.column+(i?0:1):r.indented+(i?0:o):r.indented+(/^(?:case|default)\b/.test(t)?o:2*o)},electricChars:"{}",blockCommentStart:"/*",blockCommentEnd:"*/",lineComment:"//"}}),e.defineMIME("text/x-haxe","haxe"),e.defineMode("hxml",function(){return{startState:function(){return{define:!1,inString:!1}},token:function(e,t){var n=e.peek(),r=e.sol();if("#"==n)return e.skipToEnd(),"comment";if(r&&"-"==n){var a="variable-2";return e.eat(/-/),"-"==e.peek()&&(e.eat(/-/),a="keyword a"),"D"==e.peek()&&(e.eat(/[D]/),a="keyword c",t.define=!0),e.eatWhile(/[A-Z]/i),a}n=e.peek();return 0==t.inString&&"'"==n&&(t.inString=!0,e.next()),1==t.inString?(e.skipTo("'")||e.skipToEnd(),"'"==e.peek()&&(e.next(),t.inString=!1),"string"):(e.next(),null)},lineComment:"#"}}),e.defineMIME("text/x-hxml","hxml")});