!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror"),require("../fold/xml-fold")):"function"==typeof define&&define.amd?define(["../../lib/codemirror","../fold/xml-fold"],e):e(CodeMirror)}(function(v){v.defineOption("autoCloseTags",!1,function(e,t,n){if(n!=v.Init&&n&&e.removeKeyMap("autoCloseTags"),t){var o={name:"autoCloseTags"};"object"==typeof t&&!t.whenClosing||(o["'/'"]=function(e){return function(e){return e.getOption("disableInput")?v.Pass:a(e,!0)}(e)}),"object"==typeof t&&!t.whenOpening||(o["'>'"]=function(e){return function(e){if(e.getOption("disableInput"))return v.Pass;for(var t=e.listSelections(),n=[],o=e.getOption("autoCloseTags"),a=0;a<t.length;a++){if(!t[a].empty())return v.Pass;var r=t[a].head,i=e.getTokenAt(r),s=v.innerMode(e.getMode(),i.state),l=s.state;if("xml"!=s.mode.name||!l.tagName)return v.Pass;var c="html"==s.mode.configuration,d="object"==typeof o&&o.dontCloseTags||c&&x,f="object"==typeof o&&o.indentTags||c&&P,g=l.tagName;i.end>r.ch&&(g=g.slice(0,g.length-i.end+r.ch));var u=g.toLowerCase();if(!g||"string"==i.type&&(i.end!=r.ch||!/[\"\']/.test(i.string.charAt(i.string.length-1))||1==i.string.length)||"tag"==i.type&&"closeTag"==l.type||i.string.indexOf("/")==i.string.length-1||d&&-1<T(d,u)||C(e,g,r,l,!0))return v.Pass;var m="object"==typeof o&&o.emptyTags;if(m&&-1<T(m,g))n[a]={text:"/>",newPos:v.Pos(r.line,r.ch+2)};else{var h=f&&-1<T(f,u);n[a]={indent:h,text:">"+(h?"\n\n":"")+"</"+g+">",newPos:h?v.Pos(r.line+1,0):v.Pos(r.line,r.ch+1)}}}var p="object"==typeof o&&o.dontIndentOnAutoClose;for(a=t.length-1;0<=a;a--){var y=n[a];e.replaceRange(y.text,t[a].head,t[a].anchor,"+insert");var b=e.listSelections().slice(0);b[a]={head:y.newPos,anchor:y.newPos},e.setSelections(b),!p&&y.indent&&(e.indentLine(y.newPos.line,null,!0),e.indentLine(y.newPos.line+1,null,!0))}}(e)}),e.addKeyMap(o)}});var x=["area","base","br","col","command","embed","hr","img","input","keygen","link","meta","param","source","track","wbr"],P=["applet","blockquote","body","button","div","dl","fieldset","form","frameset","h1","h2","h3","h4","h5","h6","head","html","iframe","layer","legend","object","ol","p","select","table","ul"];function a(e,t){for(var n=e.listSelections(),o=[],a=t?"/":"</",r=e.getOption("autoCloseTags"),i="object"==typeof r&&r.dontIndentOnSlash,s=0;s<n.length;s++){if(!n[s].empty())return v.Pass;var l,c=n[s].head,d=e.getTokenAt(c),f=v.innerMode(e.getMode(),d.state),g=f.state;if(t&&("string"==d.type||"<"!=d.string.charAt(0)||d.start!=c.ch-1))return v.Pass;if("xml"!=f.mode.name)if("htmlmixed"==e.getMode().name&&"javascript"==f.mode.name)l=a+"script";else{if("htmlmixed"!=e.getMode().name||"css"!=f.mode.name)return v.Pass;l=a+"style"}else{if(!g.context||!g.context.tagName||C(e,g.context.tagName,c,g))return v.Pass;l=a+g.context.tagName}">"!=e.getLine(c.line).charAt(d.end)&&(l+=">"),o[s]=l}if(e.replaceSelections(o),n=e.listSelections(),!i)for(s=0;s<n.length;s++)(s==n.length-1||n[s].head.line<n[s+1].head.line)&&e.indentLine(n[s].head.line)}function T(e,t){if(e.indexOf)return e.indexOf(t);for(var n=0,o=e.length;n<o;++n)if(e[n]==t)return n;return-1}function C(e,t,n,o,a){if(!v.scanForClosingTag)return!1;var r=Math.min(e.lastLine()+1,n.line+500),i=v.scanForClosingTag(e,n,null,r);if(!i||i.tag!=t)return!1;for(var s=o.context,l=a?1:0;s&&s.tagName==t;s=s.prev)++l;n=i.to;for(var c=1;c<l;c++){var d=v.scanForClosingTag(e,n,null,r);if(!d||d.tag!=t)return!1;n=d.to}return!0}v.commands.closeTag=function(e){return a(e)}});