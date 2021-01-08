!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("scheme",function(){var m="comment",f="string",h="atom",g="number",x="bracket";function e(e){for(var t={},n=e.split(" "),r=0;r<n.length;++r)t[n[r]]=!0;return t}var b=e("λ case-lambda call/cc class define-class exit-handler field import inherit init-field interface let*-values let-values let/ec mixin opt-lambda override protect provide public rename require require-for-syntax syntax syntax-case syntax-error unit/sig unless when with-syntax and begin call-with-current-continuation call-with-input-file call-with-output-file case cond define define-syntax delay do dynamic-wind else for-each if lambda let let* let-syntax letrec letrec-syntax map or syntax-rules abs acos angle append apply asin assoc assq assv atan boolean? caar cadr call-with-input-file call-with-output-file call-with-values car cdddar cddddr cdr ceiling char->integer char-alphabetic? char-ci<=? char-ci<? char-ci=? char-ci>=? char-ci>? char-downcase char-lower-case? char-numeric? char-ready? char-upcase char-upper-case? char-whitespace? char<=? char<? char=? char>=? char>? char? close-input-port close-output-port complex? cons cos current-input-port current-output-port denominator display eof-object? eq? equal? eqv? eval even? exact->inexact exact? exp expt #f floor force gcd imag-part inexact->exact inexact? input-port? integer->char integer? interaction-environment lcm length list list->string list->vector list-ref list-tail list? load log magnitude make-polar make-rectangular make-string make-vector max member memq memv min modulo negative? newline not null-environment null? number->string number? numerator odd? open-input-file open-output-file output-port? pair? peek-char port? positive? procedure? quasiquote quote quotient rational? rationalize read read-char real-part real? remainder reverse round scheme-report-environment set! set-car! set-cdr! sin sqrt string string->list string->number string->symbol string-append string-ci<=? string-ci<? string-ci=? string-ci>=? string-ci>? string-copy string-fill! string-length string-ref string-set! string<=? string<? string=? string>=? string>? string? substring symbol->string symbol? #t tan transcript-off transcript-on truncate values vector vector->list vector-fill! vector-length vector-ref vector-set! with-input-from-file with-output-to-file write write-char zero?"),v=e("define let letrec let* lambda");function r(e,t,n){this.indent=e,this.type=t,this.prev=n}function k(e,t,n){e.indentStack=new r(t,n,e.indentStack)}var t=new RegExp(/^(?:[-+]i|[-+][01]+#*(?:\/[01]+#*)?i|[-+]?[01]+#*(?:\/[01]+#*)?@[-+]?[01]+#*(?:\/[01]+#*)?|[-+]?[01]+#*(?:\/[01]+#*)?[-+](?:[01]+#*(?:\/[01]+#*)?)?i|[-+]?[01]+#*(?:\/[01]+#*)?)(?=[()\s;"]|$)/i),n=new RegExp(/^(?:[-+]i|[-+][0-7]+#*(?:\/[0-7]+#*)?i|[-+]?[0-7]+#*(?:\/[0-7]+#*)?@[-+]?[0-7]+#*(?:\/[0-7]+#*)?|[-+]?[0-7]+#*(?:\/[0-7]+#*)?[-+](?:[0-7]+#*(?:\/[0-7]+#*)?)?i|[-+]?[0-7]+#*(?:\/[0-7]+#*)?)(?=[()\s;"]|$)/i),i=new RegExp(/^(?:[-+]i|[-+][\da-f]+#*(?:\/[\da-f]+#*)?i|[-+]?[\da-f]+#*(?:\/[\da-f]+#*)?@[-+]?[\da-f]+#*(?:\/[\da-f]+#*)?|[-+]?[\da-f]+#*(?:\/[\da-f]+#*)?[-+](?:[\da-f]+#*(?:\/[\da-f]+#*)?)?i|[-+]?[\da-f]+#*(?:\/[\da-f]+#*)?)(?=[()\s;"]|$)/i),a=new RegExp(/^(?:[-+]i|[-+](?:(?:(?:\d+#+\.?#*|\d+\.\d*#*|\.\d+#*|\d+)(?:[esfdl][-+]?\d+)?)|\d+#*\/\d+#*)i|[-+]?(?:(?:(?:\d+#+\.?#*|\d+\.\d*#*|\.\d+#*|\d+)(?:[esfdl][-+]?\d+)?)|\d+#*\/\d+#*)@[-+]?(?:(?:(?:\d+#+\.?#*|\d+\.\d*#*|\.\d+#*|\d+)(?:[esfdl][-+]?\d+)?)|\d+#*\/\d+#*)|[-+]?(?:(?:(?:\d+#+\.?#*|\d+\.\d*#*|\.\d+#*|\d+)(?:[esfdl][-+]?\d+)?)|\d+#*\/\d+#*)[-+](?:(?:(?:\d+#+\.?#*|\d+\.\d*#*|\.\d+#*|\d+)(?:[esfdl][-+]?\d+)?)|\d+#*\/\d+#*)?i|(?:(?:(?:\d+#+\.?#*|\d+\.\d*#*|\.\d+#*|\d+)(?:[esfdl][-+]?\d+)?)|\d+#*\/\d+#*))(?=[()\s;"]|$)/i);function y(e){return e.match(t)}function w(e){return e.match(n)}function E(e,t){return!0===t&&e.backUp(1),e.match(a)}function q(e){return e.match(i)}return{startState:function(){return{indentStack:null,indentation:0,mode:!1,sExprComment:!1,sExprQuote:!1}},token:function(e,t){if(null==t.indentStack&&e.sol()&&(t.indentation=e.indentation()),e.eatSpace())return null;var n=null;switch(t.mode){case"string":for(var r=!1;null!=(i=e.next());){if('"'==i&&!r){t.mode=!1;break}r=!r&&"\\"==i}n=f;break;case"comment":for(var i,a=!1;null!=(i=e.next());){if("#"==i&&a){t.mode=!1;break}a="|"==i}n=m;break;case"s-expr-comment":if(t.mode=!1,"("!=e.peek()&&"["!=e.peek()){e.eatWhile(/[^\s\(\)\[\]]/),n=m;break}t.sExprComment=0;default:var c=e.next();if('"'==c)t.mode="string",n=f;else if("'"==c)n=("("==e.peek()||"["==e.peek()?"number"!=typeof t.sExprQuote&&(t.sExprQuote=0):e.eatWhile(/[\w_\-!$%&*+\.\/:<=>?@\^~]/),h);else if("#"==c)if(e.eat("|"))t.mode="comment",n=m;else if(e.eat(/[tf]/i))n=h;else if(e.eat(";"))t.mode="s-expr-comment",n=m;else{var o=null,s=!1,l=!0;e.eat(/[ei]/i)?s=!0:e.backUp(1),e.match(/^#b/i)?o=y:e.match(/^#o/i)?o=w:e.match(/^#x/i)?o=q:e.match(/^#d/i)?o=E:e.match(/^[-+0-9.]/,!1)?(l=!1,o=E):s||e.eat("#"),null!=o&&(l&&!s&&e.match(/^#[ei]/i),o(e)&&(n=g))}else if(/^[-+0-9.]/.test(c)&&E(e,!0))n=g;else if(";"==c)e.skipToEnd(),n=m;else if("("==c||"["==c){for(var d,u="",p=e.column();null!=(d=e.eat(/[^\s\(\[\;\)\]]/));)u+=d;0<u.length&&v.propertyIsEnumerable(u)?k(t,p+2,c):(e.eatSpace(),e.eol()||";"==e.peek()?k(t,p+1,c):k(t,p+e.current().length,c)),e.backUp(e.current().length-1),"number"==typeof t.sExprComment&&t.sExprComment++,"number"==typeof t.sExprQuote&&t.sExprQuote++,n=x}else")"==c||"]"==c?(n=x,null!=t.indentStack&&t.indentStack.type==(")"==c?"(":"[")&&(function(e){e.indentStack=e.indentStack.prev}(t),"number"==typeof t.sExprComment&&0==--t.sExprComment&&(n=m,t.sExprComment=!1),"number"==typeof t.sExprQuote&&0==--t.sExprQuote&&(n=h,t.sExprQuote=!1))):(e.eatWhile(/[\w_\-!$%&*+\.\/:<=>?@\^~]/),n=b&&b.propertyIsEnumerable(e.current())?"builtin":"variable")}return"number"==typeof t.sExprComment?m:"number"==typeof t.sExprQuote?h:n},indent:function(e){return null==e.indentStack?e.indentation:e.indentStack.indent},closeBrackets:{pairs:'()[]{}""'},lineComment:";;"}}),e.defineMIME("text/x-scheme","scheme")});