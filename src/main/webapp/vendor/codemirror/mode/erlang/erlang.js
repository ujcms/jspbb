!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(q){"use strict";q.defineMIME("text/x-erlang","erlang"),q.defineMode("erlang",function(c){var o=["-type","-spec","-export_type","-opaque"],a=["after","begin","catch","case","cond","end","fun","if","let","of","query","receive","try","when"],u=/[\->,;]/,s=["->",";",","],l=["and","andalso","band","bnot","bor","bsl","bsr","bxor","div","not","or","orelse","rem","xor"],_=/[\+\-\*\/<>=\|:!]/,f=["=","+","-","*","/",">",">=","<","=<","=:=","==","=/=","/=","||","<-","!"],p=/[<\(\[\{]/,m=["<<","(","[","{"],d=/[>\)\]\}]/,b=["}","]",")",">>"],g=["is_atom","is_binary","is_bitstring","is_boolean","is_float","is_function","is_integer","is_list","is_number","is_pid","is_port","is_record","is_reference","is_tuple","atom","binary","bitstring","boolean","function","integer","list","number","pid","port","record","reference","tuple"],k=["abs","adler32","adler32_combine","alive","apply","atom_to_binary","atom_to_list","binary_to_atom","binary_to_existing_atom","binary_to_list","binary_to_term","bit_size","bitstring_to_list","byte_size","check_process_code","contact_binary","crc32","crc32_combine","date","decode_packet","delete_module","disconnect_node","element","erase","exit","float","float_to_list","garbage_collect","get","get_keys","group_leader","halt","hd","integer_to_list","internal_bif","iolist_size","iolist_to_binary","is_alive","is_atom","is_binary","is_bitstring","is_boolean","is_float","is_function","is_integer","is_list","is_number","is_pid","is_port","is_process_alive","is_record","is_reference","is_tuple","length","link","list_to_atom","list_to_binary","list_to_bitstring","list_to_existing_atom","list_to_float","list_to_integer","list_to_pid","list_to_tuple","load_module","make_ref","module_loaded","monitor_node","node","node_link","node_unlink","nodes","notalive","now","open_port","pid_to_list","port_close","port_command","port_connect","port_control","pre_loaded","process_flag","process_info","processes","purge_module","put","register","registered","round","self","setelement","size","spawn","spawn_link","spawn_monitor","spawn_opt","split_binary","statistics","term_to_binary","time","throw","tl","trunc","tuple_size","tuple_to_list","unlink","unregister","whereis"],h=/[\w@Ø-ÞÀ-Öß-öø-ÿ]/,y=/[0-7]{1,3}|[bdefnrstv\\"']|\^[a-zA-Z]|x[0-9a-zA-Z]{2}|x{[0-9a-zA-Z]+}/;function v(e,t,n){if(1==e.current().length&&t.test(e.current())){for(e.backUp(1);t.test(e.peek());)if(e.next(),z(e.current(),n))return!0;e.backUp(e.current().length-1)}return!1}function w(e,t,n){if(1==e.current().length&&t.test(e.current())){for(;t.test(e.peek());)e.next();for(;0<e.current().length;){if(z(e.current(),n))return!0;e.backUp(1)}e.next()}return!1}function x(e){return t(e,'"',"\\")}function S(e){return t(e,"'","\\")}function t(e,t,n){for(;!e.eol();){var r=e.next();if(r==t)return!0;r==n&&e.next()}return!1}function z(e,t){return-1<t.indexOf(e)}function W(e,t,n){switch(function(e,t){"comment"!=t.type&&"whitespace"!=t.type&&(e.tokenStack=function(e,t){var n=e.length-1;0<n&&"record"===e[n].type&&"dot"===t.type?e.pop():(0<n&&"group"===e[n].type&&e.pop(),e.push(t));return e}(e.tokenStack,t),e.tokenStack=function(e){if(!e.length)return e;var t=e.length-1;if("dot"===e[t].type)return[];if(1<t&&"fun"===e[t].type&&"fun"===e[t-1].token)return e.slice(0,t-1);switch(e[t].token){case"}":return r(e,{g:["{"]});case"]":return r(e,{i:["["]});case")":return r(e,{i:["("]});case">>":return r(e,{i:["<<"]});case"end":return r(e,{i:["begin","case","fun","if","receive","try"]});case",":return r(e,{e:["begin","try","when","->",",","(","[","{","<<"]});case"->":return r(e,{r:["when"],m:["try","if","case","receive"]});case";":return r(e,{E:["case","fun","if","receive","try","when"]});case"catch":return r(e,{e:["try"]});case"of":return r(e,{e:["case"]});case"after":return r(e,{e:["receive","try"]});default:return e}}(e.tokenStack))}(e,function(e,t){return U(t.current(),t.column(),t.indentation(),e)}(n,t)),n){case"atom":return"atom";case"attribute":return"attribute";case"boolean":return"atom";case"builtin":return"builtin";case"close_paren":case"colon":return null;case"comment":return"comment";case"dot":return null;case"error":return"error";case"fun":return"meta";case"function":return"tag";case"guard":return"property";case"keyword":return"keyword";case"macro":return"variable-2";case"number":return"number";case"open_paren":return null;case"operator":return"operator";case"record":return"bracket";case"separator":return null;case"string":return"string";case"type":return"def";case"variable":return"variable";default:return null}}function U(e,t,n,r){return{token:e,column:t,indent:n,type:r}}function E(e,t){var n=e.tokenStack.length,r=t||1;return!(n<r)&&e.tokenStack[n-r]}function r(e,t){for(var n in t)for(var r=e.length-1,i=t[n],o=r-1;-1<o;o--)if(z(e[o].token,i)){var a=e.slice(0,o);switch(n){case"m":return a.concat(e[o]).concat(e[r]);case"r":return a.concat(e[r]);case"i":return a;case"g":return a.concat(U(c="group",0,0,c));case"E":case"e":return a.concat(e[o])}}var c;return"E"==n?[]:e}function n(e,t){var n,r=c.indentUnit,i=function(e){var t=e.match(/,|[a-z]+|\}|\]|\)|>>|\|+|\(/);return M(t)&&0===t.index?t[0]:""}(t),o=E(e,1),a=E(e,2);return e.in_string||e.in_atom?q.Pass:a?"when"==o.token?o.column+r:"when"===i&&"function"===a.type?a.indent+r:"("===i&&"fun"===o.token?o.column+3:"catch"===i&&(n=A(e,["try"]))?n.column:z(i,["end","after","of"])?(n=A(e,["begin","case","fun","if","receive","try"]))?n.column:q.Pass:z(i,b)?(n=A(e,m))?n.column:q.Pass:z(o.token,[",","|","||"])||z(i,[",","|","||"])?(n=function(e){var t=e.tokenStack.slice(0,-1),n=Z(t,"type",["open_paren"]);return!!M(t[n])&&t[n]}(e))?n.column+n.token.length:r:"->"==o.token?z(a.token,["receive","case","if","try"])?a.column+r+r:a.column+r:z(o.token,m)?o.column+o.token.length:M(n=function(e){var t=e.tokenStack,n=Z(t,"type",["open_paren","separator","keyword"]),r=Z(t,"type",["operator"]);return M(n)&&M(r)&&n<r?t[n+1]:!!M(n)&&t[n]}(e))?n.column+r:0:0}function A(e,t){var n=e.tokenStack,r=Z(n,"token",t);return!!M(n[r])&&n[r]}function Z(e,t,n){for(var r=e.length-1;-1<r;r--)if(z(e[r][t],n))return r;return!1}function M(e){return!1!==e&&null!=e}return{startState:function(){return{tokenStack:[],in_string:!1,in_atom:!1}},token:function(e,t){return function(e,t){if(t.in_string)return t.in_string=!x(e),W(t,e,"string");if(t.in_atom)return t.in_atom=!S(e),W(t,e,"atom");if(e.eatSpace())return W(t,e,"whitespace");if(!E(t)&&e.match(/-\s*[a-zß-öø-ÿ][\wØ-ÞÀ-Öß-öø-ÿ]*/))return z(e.current(),o)?W(t,e,"type"):W(t,e,"attribute");var n=e.next();if("%"==n)return e.skipToEnd(),W(t,e,"comment");if(":"==n)return W(t,e,"colon");if("?"==n)return e.eatSpace(),e.eatWhile(h),W(t,e,"macro");if("#"==n)return e.eatSpace(),e.eatWhile(h),W(t,e,"record");if("$"==n)return"\\"!=e.next()||e.match(y)?W(t,e,"number"):W(t,e,"error");if("."==n)return W(t,e,"dot");if("'"==n){if(!(t.in_atom=!S(e))){if(e.match(/\s*\/\s*[0-9]/,!1))return e.match(/\s*\/\s*[0-9]/,!0),W(t,e,"fun");if(e.match(/\s*\(/,!1)||e.match(/\s*:/,!1))return W(t,e,"function")}return W(t,e,"atom")}if('"'==n)return t.in_string=!x(e),W(t,e,"string");if(/[A-Z_Ø-ÞÀ-Ö]/.test(n))return e.eatWhile(h),W(t,e,"variable");if(/[a-z_ß-öø-ÿ]/.test(n)){if(e.eatWhile(h),e.match(/\s*\/\s*[0-9]/,!1))return e.match(/\s*\/\s*[0-9]/,!0),W(t,e,"fun");var r=e.current();return z(r,a)?W(t,e,"keyword"):z(r,l)?W(t,e,"operator"):e.match(/\s*\(/,!1)?!z(r,k)||":"==E(t).token&&"erlang"!=E(t,2).token?z(r,g)?W(t,e,"guard"):W(t,e,"function"):W(t,e,"builtin"):":"==function(e){var t=e.match(/([\n\s]+|%[^\n]*\n)*(.)/,!1);return t?t.pop():""}(e)?W(t,e,"erlang"==r?"builtin":"function"):z(r,["true","false"])?W(t,e,"boolean"):W(t,e,"atom")}var i=/[0-9]/;return i.test(n)?(e.eatWhile(i),e.eat("#")?e.eatWhile(/[0-9a-zA-Z]/)||e.backUp(1):e.eat(".")&&(e.eatWhile(i)?e.eat(/[eE]/)&&(e.eat(/[-+]/)?e.eatWhile(i)||e.backUp(2):e.eatWhile(i)||e.backUp(1)):e.backUp(1)),W(t,e,"number")):v(e,p,m)?W(t,e,"open_paren"):v(e,d,b)?W(t,e,"close_paren"):w(e,u,s)?W(t,e,"separator"):w(e,_,f)?W(t,e,"operator"):W(t,e,null)}(e,t)},indent:function(e,t){return n(e,t)},lineComment:"%"}})});