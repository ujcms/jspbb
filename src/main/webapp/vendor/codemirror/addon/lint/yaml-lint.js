!function(o){"object"==typeof exports&&"object"==typeof module?o(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],o):o(CodeMirror)}(function(t){"use strict";t.registerHelper("lint","yaml",function(o){var e=[];if(!window.jsyaml)return window.console&&window.console.error("Error: window.jsyaml not defined, CodeMirror YAML linting cannot run."),e;try{jsyaml.loadAll(o)}catch(o){var r=o.mark,n=r?t.Pos(r.line,r.column):t.Pos(0,0),i=n;e.push({from:n,to:i,message:o.message})}return e})});