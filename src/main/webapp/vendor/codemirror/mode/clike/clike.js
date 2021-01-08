!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(D){"use strict";function L(e,t,n,r,o,a){this.indented=e,this.column=t,this.type=n,this.info=r,this.align=o,this.prev=a}function P(e,t,n,r){var o=e.indented;return e.context&&"statement"==e.context.type&&"statement"!=n&&(o=e.context.indented),e.context=new L(o,t,n,r,null,e.context)}function E(e){var t=e.context.type;return")"!=t&&"]"!=t&&"}"!=t||(e.indented=e.context.indented),e.context=e.context.prev}function z(e,t,n){return"variable"==t.prevToken||"type"==t.prevToken||(!!/\S(?:[^- ]>|[*\]])\s*$|\*$/.test(e.string.slice(0,n))||(!(!t.typeAtEndOfLine||e.column()!=e.indentation())||void 0))}function F(e){for(;;){if(!e||"top"==e.type)return!0;if("}"==e.type&&"namespace"!=e.prev.info)return!1;e=e.prev}}function e(e){for(var t={},n=e.split(" "),r=0;r<n.length;++r)t[n[r]]=!0;return t}function R(e,t){return"function"==typeof e?e(t):e.propertyIsEnumerable(t)}D.defineMode("clike",function(e,l){var a,i,s=e.indentUnit,c=l.statementIndentUnit||s,u=l.dontAlignCalls,d=l.keywords||{},f=l.types||{},p=l.builtin||{},m=l.blockKeywords||{},h=l.defKeywords||{},y=l.atoms||{},g=l.hooks||{},x=l.multiLineStrings,b=!1!==l.indentStatements,t=!1!==l.indentSwitch,k=l.namespaceSeparator,v=l.isPunctuationChar||/[\[\]{}\(\),;\:\.]/,_=l.numberStart||/[\d\.]/,w=l.number||/^(?:0x[a-f\d]+|0b[01]+|(?:\d+\.?\d*|\.\d+)(?:e[-+]?\d+)?)(u|ll?|l|f)?/i,S=l.isOperatorChar||/[+\-*&%=<>!?|\/]/,T=l.isIdentifierChar||/[\w\$_\xa1-\uffff]/,C=l.isReservedIdentifier||!1;function I(e,t){var n=e.next();if(g[n]){var r=g[n](e,t);if(!1!==r)return r}if('"'==n||"'"==n)return t.tokenize=function(a){return function(e,t){for(var n,r=!1,o=!1;null!=(n=e.next());){if(n==a&&!r){o=!0;break}r=!r&&"\\"==n}return!o&&(r||x)||(t.tokenize=null),"string"}}(n),t.tokenize(e,t);if(v.test(n))return a=n,null;if(_.test(n)){if(e.backUp(1),e.match(w))return"number";e.next()}if("/"==n){if(e.eat("*"))return(t.tokenize=M)(e,t);if(e.eat("/"))return e.skipToEnd(),"comment"}if(S.test(n)){for(;!e.match(/^\/[\/*]/,!1)&&e.eat(S););return"operator"}if(e.eatWhile(T),k)for(;e.match(k);)e.eatWhile(T);var o=e.current();return R(d,o)?(R(m,o)&&(a="newstatement"),R(h,o)&&(i=!0),"keyword"):R(f,o)?"type":R(p,o)||C&&C(o)?(R(m,o)&&(a="newstatement"),"builtin"):R(y,o)?"atom":"variable"}function M(e,t){for(var n,r=!1;n=e.next();){if("/"==n&&r){t.tokenize=null;break}r="*"==n}return"comment"}function N(e,t){l.typeFirstDefinitions&&e.eol()&&F(t.context)&&(t.typeAtEndOfLine=z(e,t,e.pos))}return{startState:function(e){return{tokenize:null,context:new L((e||0)-s,0,"top",null,!1),indented:0,startOfLine:!0,prevToken:null}},token:function(e,t){var n=t.context;if(e.sol()&&(null==n.align&&(n.align=!1),t.indented=e.indentation(),t.startOfLine=!0),e.eatSpace())return N(e,t),null;a=i=null;var r=(t.tokenize||I)(e,t);if("comment"==r||"meta"==r)return r;if(null==n.align&&(n.align=!0),";"==a||":"==a||","==a&&e.match(/^\s*(?:\/\/.*)?$/,!1))for(;"statement"==t.context.type;)E(t);else if("{"==a)P(t,e.column(),"}");else if("["==a)P(t,e.column(),"]");else if("("==a)P(t,e.column(),")");else if("}"==a){for(;"statement"==n.type;)n=E(t);for("}"==n.type&&(n=E(t));"statement"==n.type;)n=E(t)}else a==n.type?E(t):b&&(("}"==n.type||"top"==n.type)&&";"!=a||"statement"==n.type&&"newstatement"==a)&&P(t,e.column(),"statement",e.current());if("variable"==r&&("def"==t.prevToken||l.typeFirstDefinitions&&z(e,t,e.start)&&F(t.context)&&e.match(/^\s*\(/,!1))&&(r="def"),g.token){var o=g.token(e,t,r);void 0!==o&&(r=o)}return"def"==r&&!1===l.styleDefs&&(r="variable"),t.startOfLine=!1,t.prevToken=i?"def":r||a,N(e,t),r},indent:function(e,t){if(e.tokenize!=I&&null!=e.tokenize||e.typeAtEndOfLine)return D.Pass;var n=e.context,r=t&&t.charAt(0),o=r==n.type;if("statement"==n.type&&"}"==r&&(n=n.prev),l.dontIndentStatements)for(;"statement"==n.type&&l.dontIndentStatements.test(n.info);)n=n.prev;if(g.indent){var a=g.indent(e,n,t,s);if("number"==typeof a)return a}var i=n.prev&&"switch"==n.prev.info;if(l.allmanIndentation&&/[{(]/.test(r)){for(;"top"!=n.type&&"}"!=n.type;)n=n.prev;return n.indented}return"statement"==n.type?n.indented+("{"==r?0:c):!n.align||u&&")"==n.type?")"!=n.type||o?n.indented+(o?0:s)+(o||!i||/^(?:case|default)\b/.test(t)?0:s):n.indented+c:n.column+(o?0:1)},electricInput:t?/^\s*(?:case .*?:|default:|\{\}?|\})$/:/^\s*[{}]$/,blockCommentStart:"/*",blockCommentEnd:"*/",blockCommentContinue:" * ",lineComment:"//",fold:"brace"}});var t="auto if break case register continue return default do sizeof static else struct switch extern typedef union for goto while enum const volatile inline restrict asm fortran",n=e("int long char short double float unsigned signed void bool"),r=e("SEL instancetype id Class Protocol BOOL");function o(e){return R(n,e)||/.+_t$/.test(e)}var a="case do else for if switch while struct enum union",i="struct enum union";function l(e,t){if(!t.startOfLine)return!1;for(var n,r=null;n=e.peek();){if("\\"==n&&e.match(/^.$/)){r=l;break}if("/"==n&&e.match(/^\/[\/\*]/,!1))break;e.next()}return t.tokenize=r,"meta"}function s(e,t){return"type"==t.prevToken&&"type"}function c(e){return!(!e||e.length<2)&&("_"==e[0]&&("_"==e[1]||e[1]!==e[1].toLowerCase()))}function u(e){return e.eatWhile(/[\w\.']/),"number"}function d(e,t){if(e.backUp(1),e.match(/(R|u8R|uR|UR|LR)/)){var n=e.match(/"([^\s\\()]{0,16})\(/);return!!n&&(t.cpp11RawStringDelim=n[1],(t.tokenize=p)(e,t))}return e.match(/(u8|u|U|L)/)?!!e.match(/["']/,!1)&&"string":(e.next(),!1)}function f(e,t){for(var n;null!=(n=e.next());)if('"'==n&&!e.eat('"')){t.tokenize=null;break}return"string"}function p(e,t){var n=t.cpp11RawStringDelim.replace(/[^\w\s]/g,"\\$&");return e.match(new RegExp(".*?\\)"+n+'"'))?t.tokenize=null:e.skipToEnd(),"string"}function m(e,t){"string"==typeof e&&(e=[e]);var n=[];function r(e){if(e)for(var t in e)e.hasOwnProperty(t)&&n.push(t)}r(t.keywords),r(t.types),r(t.builtin),r(t.atoms),n.length&&(t.helperType=e[0],D.registerHelper("hintWords",e[0],n));for(var o=0;o<e.length;++o)D.defineMIME(e[o],t)}function h(e,t){for(var n=!1;!e.eol();){if(!n&&e.match('"""')){t.tokenize=null;break}n="\\"==e.next()&&!n}return"string"}m(["text/x-csrc","text/x-c","text/x-chdr"],{name:"clike",keywords:e(t),types:o,blockKeywords:e(a),defKeywords:e(i),typeFirstDefinitions:!0,atoms:e("NULL true false"),isReservedIdentifier:c,hooks:{"#":l,"*":s},modeProps:{fold:["brace","include"]}}),m(["text/x-c++src","text/x-c++hdr"],{name:"clike",keywords:e(t+"alignas alignof and and_eq audit axiom bitand bitor catch class compl concept constexpr const_cast decltype delete dynamic_cast explicit export final friend import module mutable namespace new noexcept not not_eq operator or or_eq override private protected public reinterpret_cast requires static_assert static_cast template this thread_local throw try typeid typename using virtual xor xor_eq"),types:o,blockKeywords:e(a+" class try catch"),defKeywords:e(i+" class namespace"),typeFirstDefinitions:!0,atoms:e("true false NULL nullptr"),dontIndentStatements:/^template$/,isIdentifierChar:/[\w\$_~\xa1-\uffff]/,isReservedIdentifier:c,hooks:{"#":l,"*":s,u:d,U:d,L:d,R:d,0:u,1:u,2:u,3:u,4:u,5:u,6:u,7:u,8:u,9:u,token:function(e,t,n){if("variable"==n&&"("==e.peek()&&(";"==t.prevToken||null==t.prevToken||"}"==t.prevToken)&&function(e){var t=/(\w+)::~?(\w+)$/.exec(e);return t&&t[1]==t[2]}(e.current()))return"def"}},namespaceSeparator:"::",modeProps:{fold:["brace","include"]}}),m("text/x-java",{name:"clike",keywords:e("abstract assert break case catch class const continue default do else enum extends final finally for goto if implements import instanceof interface native new package private protected public return static strictfp super switch synchronized this throw throws transient try volatile while @interface"),types:e("byte short int long float double boolean char void Boolean Byte Character Double Float Integer Long Number Object Short String StringBuffer StringBuilder Void"),blockKeywords:e("catch class do else finally for if switch try while"),defKeywords:e("class interface enum @interface"),typeFirstDefinitions:!0,atoms:e("true false null"),number:/^(?:0x[a-f\d_]+|0b[01_]+|(?:[\d_]+\.?\d*|\.\d+)(?:e[-+]?[\d_]+)?)(u|ll?|l|f)?/i,hooks:{"@":function(e){return!e.match("interface",!1)&&(e.eatWhile(/[\w\$_]/),"meta")}},modeProps:{fold:["brace","import"]}}),m("text/x-csharp",{name:"clike",keywords:e("abstract as async await base break case catch checked class const continue default delegate do else enum event explicit extern finally fixed for foreach goto if implicit in interface internal is lock namespace new operator out override params private protected public readonly ref return sealed sizeof stackalloc static struct switch this throw try typeof unchecked unsafe using virtual void volatile while add alias ascending descending dynamic from get global group into join let orderby partial remove select set value var yield"),types:e("Action Boolean Byte Char DateTime DateTimeOffset Decimal Double Func Guid Int16 Int32 Int64 Object SByte Single String Task TimeSpan UInt16 UInt32 UInt64 bool byte char decimal double short int long object sbyte float string ushort uint ulong"),blockKeywords:e("catch class do else finally for foreach if struct switch try while"),defKeywords:e("class interface namespace struct var"),typeFirstDefinitions:!0,atoms:e("true false null"),hooks:{"@":function(e,t){return e.eat('"')?(t.tokenize=f)(e,t):(e.eatWhile(/[\w\$_]/),"meta")}}}),m("text/x-scala",{name:"clike",keywords:e("abstract case catch class def do else extends final finally for forSome if implicit import lazy match new null object override package private protected return sealed super this throw trait try type val var while with yield _ assert assume require print println printf readLine readBoolean readByte readShort readChar readInt readLong readFloat readDouble"),types:e("AnyVal App Application Array BufferedIterator BigDecimal BigInt Char Console Either Enumeration Equiv Error Exception Fractional Function IndexedSeq Int Integral Iterable Iterator List Map Numeric Nil NotNull Option Ordered Ordering PartialFunction PartialOrdering Product Proxy Range Responder Seq Serializable Set Specializable Stream StringBuilder StringContext Symbol Throwable Traversable TraversableOnce Tuple Unit Vector Boolean Byte Character CharSequence Class ClassLoader Cloneable Comparable Compiler Double Exception Float Integer Long Math Number Object Package Pair Process Runtime Runnable SecurityManager Short StackTraceElement StrictMath String StringBuffer System Thread ThreadGroup ThreadLocal Throwable Triple Void"),multiLineStrings:!0,blockKeywords:e("catch class enum do else finally for forSome if match switch try while"),defKeywords:e("class enum def object package trait type val var"),atoms:e("true false null"),indentStatements:!1,indentSwitch:!1,isOperatorChar:/[+\-*&%=<>!?|\/#:@]/,hooks:{"@":function(e){return e.eatWhile(/[\w\$_]/),"meta"},'"':function(e,t){return!!e.match('""')&&(t.tokenize=h,t.tokenize(e,t))},"'":function(e){return e.eatWhile(/[\w\$_\xa1-\uffff]/),"atom"},"=":function(e,t){var n=t.context;return!("}"!=n.type||!n.align||!e.eat(">"))&&(t.context=new L(n.indented,n.column,n.type,n.info,null,n.prev),"operator")},"/":function(e,t){return!!e.eat("*")&&(t.tokenize=function r(o){return function(e,t){for(var n;n=e.next();){if("*"==n&&e.eat("/")){if(1!=o)return t.tokenize=r(o-1),t.tokenize(e,t);t.tokenize=null;break}if("/"==n&&e.eat("*"))return t.tokenize=r(o+1),t.tokenize(e,t)}return"comment"}}(1),t.tokenize(e,t))}},modeProps:{closeBrackets:{pairs:'()[]{}""',triples:'"'}}}),m("text/x-kotlin",{name:"clike",keywords:e("package as typealias class interface this super val operator var fun for is in This throw return annotation break continue object if else while do try when !in !is as? file import where by get set abstract enum open inner override private public internal protected catch finally out final vararg reified dynamic companion constructor init sealed field property receiver param sparam lateinit data inline noinline tailrec external annotation crossinline const operator infix suspend actual expect setparam"),types:e("Boolean Byte Character CharSequence Class ClassLoader Cloneable Comparable Compiler Double Exception Float Integer Long Math Number Object Package Pair Process Runtime Runnable SecurityManager Short StackTraceElement StrictMath String StringBuffer System Thread ThreadGroup ThreadLocal Throwable Triple Void Annotation Any BooleanArray ByteArray Char CharArray DeprecationLevel DoubleArray Enum FloatArray Function Int IntArray Lazy LazyThreadSafetyMode LongArray Nothing ShortArray Unit"),intendSwitch:!1,indentStatements:!1,multiLineStrings:!0,number:/^(?:0x[a-f\d_]+|0b[01_]+|(?:[\d_]+(\.\d+)?|\.\d+)(?:e[-+]?[\d_]+)?)(u|ll?|l|f)?/i,blockKeywords:e("catch class do else finally for if where try while enum"),defKeywords:e("class val var object interface fun"),atoms:e("true false null this"),hooks:{"@":function(e){return e.eatWhile(/[\w\$_]/),"meta"},"*":function(e,t){return"."==t.prevToken?"variable":"operator"},'"':function(e,t){return t.tokenize=function(a){return function(e,t){for(var n,r=!1,o=!1;!e.eol();){if(!a&&!r&&e.match('"')){o=!0;break}if(a&&e.match('"""')){o=!0;break}n=e.next(),!r&&"$"==n&&e.match("{")&&e.skipTo("}"),r=!r&&"\\"==n&&!a}return!o&&a||(t.tokenize=null),"string"}}(e.match('""')),t.tokenize(e,t)},indent:function(e,t,n,r){var o=n&&n.charAt(0);return"}"!=e.prevToken&&")"!=e.prevToken||""!=n?"operator"==e.prevToken&&"}"!=n||"variable"==e.prevToken&&"."==o||("}"==e.prevToken||")"==e.prevToken)&&"."==o?2*r+t.indented:t.align&&"}"==t.type?t.indented+(e.context.type==(n||"").charAt(0)?0:r):void 0:e.indented}},modeProps:{closeBrackets:{triples:'"'}}}),m(["x-shader/x-vertex","x-shader/x-fragment"],{name:"clike",keywords:e("sampler1D sampler2D sampler3D samplerCube sampler1DShadow sampler2DShadow const attribute uniform varying break continue discard return for while do if else struct in out inout"),types:e("float int bool void vec2 vec3 vec4 ivec2 ivec3 ivec4 bvec2 bvec3 bvec4 mat2 mat3 mat4"),blockKeywords:e("for while do if else struct"),builtin:e("radians degrees sin cos tan asin acos atan pow exp log exp2 sqrt inversesqrt abs sign floor ceil fract mod min max clamp mix step smoothstep length distance dot cross normalize ftransform faceforward reflect refract matrixCompMult lessThan lessThanEqual greaterThan greaterThanEqual equal notEqual any all not texture1D texture1DProj texture1DLod texture1DProjLod texture2D texture2DProj texture2DLod texture2DProjLod texture3D texture3DProj texture3DLod texture3DProjLod textureCube textureCubeLod shadow1D shadow2D shadow1DProj shadow2DProj shadow1DLod shadow2DLod shadow1DProjLod shadow2DProjLod dFdx dFdy fwidth noise1 noise2 noise3 noise4"),atoms:e("true false gl_FragColor gl_SecondaryColor gl_Normal gl_Vertex gl_MultiTexCoord0 gl_MultiTexCoord1 gl_MultiTexCoord2 gl_MultiTexCoord3 gl_MultiTexCoord4 gl_MultiTexCoord5 gl_MultiTexCoord6 gl_MultiTexCoord7 gl_FogCoord gl_PointCoord gl_Position gl_PointSize gl_ClipVertex gl_FrontColor gl_BackColor gl_FrontSecondaryColor gl_BackSecondaryColor gl_TexCoord gl_FogFragCoord gl_FragCoord gl_FrontFacing gl_FragData gl_FragDepth gl_ModelViewMatrix gl_ProjectionMatrix gl_ModelViewProjectionMatrix gl_TextureMatrix gl_NormalMatrix gl_ModelViewMatrixInverse gl_ProjectionMatrixInverse gl_ModelViewProjectionMatrixInverse gl_TexureMatrixTranspose gl_ModelViewMatrixInverseTranspose gl_ProjectionMatrixInverseTranspose gl_ModelViewProjectionMatrixInverseTranspose gl_TextureMatrixInverseTranspose gl_NormalScale gl_DepthRange gl_ClipPlane gl_Point gl_FrontMaterial gl_BackMaterial gl_LightSource gl_LightModel gl_FrontLightModelProduct gl_BackLightModelProduct gl_TextureColor gl_EyePlaneS gl_EyePlaneT gl_EyePlaneR gl_EyePlaneQ gl_FogParameters gl_MaxLights gl_MaxClipPlanes gl_MaxTextureUnits gl_MaxTextureCoords gl_MaxVertexAttribs gl_MaxVertexUniformComponents gl_MaxVaryingFloats gl_MaxVertexTextureImageUnits gl_MaxTextureImageUnits gl_MaxFragmentUniformComponents gl_MaxCombineTextureImageUnits gl_MaxDrawBuffers"),indentSwitch:!1,hooks:{"#":l},modeProps:{fold:["brace","include"]}}),m("text/x-nesc",{name:"clike",keywords:e(t+" as atomic async call command component components configuration event generic implementation includes interface module new norace nx_struct nx_union post provides signal task uses abstract extends"),types:o,blockKeywords:e(a),atoms:e("null true false"),hooks:{"#":l},modeProps:{fold:["brace","include"]}}),m("text/x-objectivec",{name:"clike",keywords:e(t+" bycopy byref in inout oneway out self super atomic nonatomic retain copy readwrite readonly strong weak assign typeof nullable nonnull null_resettable _cmd @interface @implementation @end @protocol @encode @property @synthesize @dynamic @class @public @package @private @protected @required @optional @try @catch @finally @import @selector @encode @defs @synchronized @autoreleasepool @compatibility_alias @available"),types:function(e){return o(e)||R(r,e)},builtin:e("FOUNDATION_EXPORT FOUNDATION_EXTERN NS_INLINE NS_FORMAT_FUNCTION NS_RETURNS_RETAINED NS_ERROR_ENUM NS_RETURNS_NOT_RETAINED NS_RETURNS_INNER_POINTER NS_DESIGNATED_INITIALIZER NS_ENUM NS_OPTIONS NS_REQUIRES_NIL_TERMINATION NS_ASSUME_NONNULL_BEGIN NS_ASSUME_NONNULL_END NS_SWIFT_NAME NS_REFINED_FOR_SWIFT"),blockKeywords:e(a+" @synthesize @try @catch @finally @autoreleasepool @synchronized"),defKeywords:e(i+" @interface @implementation @protocol @class"),dontIndentStatements:/^@.*$/,typeFirstDefinitions:!0,atoms:e("YES NO NULL Nil nil true false nullptr"),isReservedIdentifier:c,hooks:{"#":l,"*":s},modeProps:{fold:["brace","include"]}}),m("text/x-squirrel",{name:"clike",keywords:e("base break clone continue const default delete enum extends function in class foreach local resume return this throw typeof yield constructor instanceof static"),types:o,blockKeywords:e("case catch class else for foreach if switch try while"),defKeywords:e("function local class"),typeFirstDefinitions:!0,atoms:e("true false null"),hooks:{"#":l},modeProps:{fold:["brace","include"]}});var y=null;m("text/x-ceylon",{name:"clike",keywords:e("abstracts alias assembly assert assign break case catch class continue dynamic else exists extends finally for function given if import in interface is let module new nonempty object of out outer package return satisfies super switch then this throw try value void while"),types:function(e){var t=e.charAt(0);return t===t.toUpperCase()&&t!==t.toLowerCase()},blockKeywords:e("case catch class dynamic else finally for function if interface module new object switch try while"),defKeywords:e("class dynamic function interface module object package value"),builtin:e("abstract actual aliased annotation by default deprecated doc final formal late license native optional sealed see serializable shared suppressWarnings tagged throws variable"),isPunctuationChar:/[\[\]{}\(\),;\:\.`]/,isOperatorChar:/[+\-*&%=<>!?|^~:\/]/,numberStart:/[\d#$]/,number:/^(?:#[\da-fA-F_]+|\$[01_]+|[\d_]+[kMGTPmunpf]?|[\d_]+\.[\d_]+(?:[eE][-+]?\d+|[kMGTPmunpf]|)|)/i,multiLineStrings:!0,typeFirstDefinitions:!0,atoms:e("true false null larger smaller equal empty finished"),indentSwitch:!1,styleDefs:!1,hooks:{"@":function(e){return e.eatWhile(/[\w\$_]/),"meta"},'"':function(e,t){return t.tokenize=function a(i){return function(e,t){for(var n,r=!1,o=!1;!e.eol();){if(!r&&e.match('"')&&("single"==i||e.match('""'))){o=!0;break}if(!r&&e.match("``")){y=a(i),o=!0;break}n=e.next(),r="single"==i&&!r&&"\\"==n}return o&&(t.tokenize=null),"string"}}(e.match('""')?"triple":"single"),t.tokenize(e,t)},"`":function(e,t){return!(!y||!e.match("`"))&&(t.tokenize=y,y=null,t.tokenize(e,t))},"'":function(e){return e.eatWhile(/[\w\$_\xa1-\uffff]/),"atom"},token:function(e,t,n){if(("variable"==n||"type"==n)&&"."==t.prevToken)return"variable-2"}},modeProps:{fold:["brace","import"],closeBrackets:{triples:'"'}}})});