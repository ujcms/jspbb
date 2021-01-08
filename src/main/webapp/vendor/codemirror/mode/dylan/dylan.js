!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";function b(e,n){for(var t=0;t<e.length;t++)n(e[t],t)}function p(e,n){for(var t=0;t<e.length;t++)if(n(e[t],t))return!0;return!1}e.defineMode("dylan",function(e){var t={unnamedDefinition:["interface"],namedDefinition:["module","library","macro","C-struct","C-union","C-function","C-callable-wrapper"],typeParameterizedDefinition:["class","C-subtype","C-mapped-subtype"],otherParameterizedDefinition:["method","function","C-variable","C-address"],constantSimpleDefinition:["constant"],variableSimpleDefinition:["variable"],otherSimpleDefinition:["generic","domain","C-pointer-type","table"],statement:["if","block","begin","method","case","for","select","when","unless","until","while","iterate","profiling","dynamic-bind"],separator:["finally","exception","cleanup","else","elseif","afterwards"],other:["above","below","by","from","handler","in","instance","let","local","otherwise","slot","subclass","then","to","keyed-by","virtual"],signalingCalls:["signal","error","cerror","break","check-type","abort"]};t.otherDefinition=t.unnamedDefinition.concat(t.namedDefinition).concat(t.otherParameterizedDefinition),t.definition=t.typeParameterizedDefinition.concat(t.otherDefinition),t.parameterizedDefinition=t.typeParameterizedDefinition.concat(t.otherParameterizedDefinition),t.simpleDefinition=t.constantSimpleDefinition.concat(t.variableSimpleDefinition).concat(t.otherSimpleDefinition),t.keyword=t.statement.concat(t.separator).concat(t.other);var n="[-_a-zA-Z?!*@<>$%]+",o=new RegExp("^"+n),a={symbolKeyword:n+":",symbolClass:"<"+n+">",symbolGlobal:"\\*"+n+"\\*",symbolConstant:"\\$"+n},f={symbolKeyword:"atom",symbolClass:"tag",symbolGlobal:"variable-2",symbolConstant:"variable-3"};for(var i in a)a.hasOwnProperty(i)&&(a[i]=new RegExp("^"+a[i]));a.keyword=[/^with(?:out)?-[-_a-zA-Z?!*@<>$%]+/];var r={keyword:"keyword",definition:"def",simpleDefinition:"def",signalingCalls:"builtin"},l={},c={};function u(e,n,t){return(n.tokenize=t)(e,n)}function s(n,e){var t=n.peek();if("'"==t||'"'==t)return n.next(),u(n,e,d(t,"string"));if("/"==t){if(n.next(),n.eat("*"))return u(n,e,m);if(n.eat("/"))return n.skipToEnd(),"comment";n.backUp(1)}else if(/[+\-\d\.]/.test(t)){if(n.match(/^[+-]?[0-9]*\.[0-9]*([esdx][+-]?[0-9]+)?/i)||n.match(/^[+-]?[0-9]+([esdx][+-]?[0-9]+)/i)||n.match(/^[+-]?\d+/))return"number"}else{if("#"==t)return n.next(),'"'==(t=n.peek())?(n.next(),u(n,e,d('"',"string"))):"b"==t?(n.next(),n.eatWhile(/[01]/),"number"):"x"==t?(n.next(),n.eatWhile(/[\da-f]/i),"number"):"o"==t?(n.next(),n.eatWhile(/[0-7]/),"number"):"#"==t?(n.next(),"punctuation"):"["==t||"("==t?(n.next(),"bracket"):n.match(/f|t|all-keys|include|key|next|rest/i)?"atom":(n.eatWhile(/[-a-zA-Z]/),"error");if("~"==t)return n.next(),"="==(t=n.peek())&&(n.next(),"="==(t=n.peek())&&n.next()),"operator";if(":"==t){if(n.next(),"="==(t=n.peek()))return n.next(),"operator";if(":"==t)return n.next(),"punctuation"}else{if(-1!="[](){}".indexOf(t))return n.next(),"bracket";if(-1!=".,".indexOf(t))return n.next(),"punctuation";if(n.match("end"))return"keyword"}}for(var i in a)if(a.hasOwnProperty(i)){var r=a[i];if(r instanceof Array&&p(r,function(e){return n.match(e)})||n.match(r))return f[i]}return/[+\-*\/^=<>&|]/.test(t)?(n.next(),"operator"):n.match("define")?"def":(n.eatWhile(/[\w\-]/),l.hasOwnProperty(n.current())?c[n.current()]:n.current().match(o)?"variable":(n.next(),"variable-2"))}function m(e,n){for(var t,i=!1,r=!1,o=0;t=e.next();){if("/"==t&&i){if(!(0<o)){n.tokenize=s;break}o--}else"*"==t&&r&&o++;i="*"==t,r="/"==t}return"comment"}function d(o,a){return function(e,n){for(var t,i=!1,r=!1;null!=(t=e.next());){if(t==o&&!i){r=!0;break}i=!i&&"\\"==t}return!r&&i||(n.tokenize=s),a}}return b(["keyword","definition","simpleDefinition","signalingCalls"],function(n){b(t[n],function(e){l[e]=n,c[e]=r[n]})}),{startState:function(){return{tokenize:s,currentIndent:0}},token:function(e,n){return e.eatSpace()?null:n.tokenize(e,n)},blockCommentStart:"/*",blockCommentEnd:"*/"}}),e.defineMIME("text/x-dylan","dylan")});