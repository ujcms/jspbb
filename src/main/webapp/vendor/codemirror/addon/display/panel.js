!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){function h(e,t,i,r){this.cm=e,this.node=t,this.options=i,this.height=r,this.cleared=!1}function l(e,t){for(var i=t.nextSibling;i;i=i.nextSibling)if(i==e.getWrapperElement())return!0;return!1}e.defineExtension("addPanel",function(e,t){t=t||{},this.state.panels||function(r){var e=r.getWrapperElement(),t=window.getComputedStyle?window.getComputedStyle(e):e.currentStyle,n=parseInt(t.height),s=r.state.panels={setHeight:e.style.height,heightLeft:n,panels:0,wrapper:document.createElement("div")};e.parentNode.insertBefore(s.wrapper,e);var i=r.hasFocus();s.wrapper.appendChild(e),i&&r.focus();r._setSize=r.setSize,null!=n&&(r.setSize=function(e,t){if(null==t)return this._setSize(e,t);if("number"!=typeof(s.setHeight=t)){var i=/^(\d+\.?\d*)px$/.exec(t);i?t=Number(i[1]):(s.wrapper.style.height=t,t=s.wrapper.offsetHeight,s.wrapper.style.height="")}r._setSize(e,s.heightLeft+=t-n),n=t})}(this);var i=this.state.panels,r=i.wrapper,n=this.getWrapperElement(),s=t.replace instanceof h&&!t.replace.cleared;t.after instanceof h&&!t.after.cleared?r.insertBefore(e,t.before.node.nextSibling):t.before instanceof h&&!t.before.cleared?r.insertBefore(e,t.before.node):s?(r.insertBefore(e,t.replace.node),i.panels++,t.replace.clear()):"bottom"==t.position?r.appendChild(e):"before-bottom"==t.position?r.insertBefore(e,n.nextSibling):"after-top"==t.position?r.insertBefore(e,n):r.insertBefore(e,r.firstChild);var o=t&&t.height||e.offsetHeight;return this._setSize(null,i.heightLeft-=o),s||i.panels++,t.stable&&l(this,e)&&this.scrollTo(null,this.getScrollInfo().top+o),new h(this,e,t,o)}),h.prototype.clear=function(){if(!this.cleared){this.cleared=!0;var e=this.cm.state.panels;this.cm._setSize(null,e.heightLeft+=this.height),this.options.stable&&l(this.cm,this.node)&&this.cm.scrollTo(null,this.cm.getScrollInfo().top-this.height),e.wrapper.removeChild(this.node),0==--e.panels&&function(e){var t=e.state.panels;e.state.panels=null;var i=e.getWrapperElement();t.wrapper.parentNode.replaceChild(i,t.wrapper),i.style.height=t.setHeight,e.setSize=e._setSize,e.setSize()}(this.cm)}},h.prototype.changed=function(e){var t=null==e?this.node.offsetHeight:e,i=this.cm.state.panels;this.cm._setSize(null,i.heightLeft-=t-this.height),this.height=t}});