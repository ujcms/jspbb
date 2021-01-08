var gulp = require('gulp');
var browserSync = require('browser-sync').create();
var runSequence = require('run-sequence');
var sass = require('gulp-sass');
var plumber = require('gulp-plumber');
var autoprefixer = require('gulp-autoprefixer');
var uglify = require('gulp-uglify');
var cleanCss = require('gulp-clean-css');
var del = require('del');

var concat = require('gulp-concat');
var buffer = require('vinyl-buffer');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var rename = require('gulp-rename');

function simplemdeTaskBrowserify(opts) {
    return browserify('src/main/webapp/js/simplemde/src/js/simplemde.js', opts).bundle();
}

gulp.task('simplemde-browserify:debug', function () {
    return simplemdeTaskBrowserify({debug: true, standalone: 'SimpleMDE'})
        .pipe(source('simplemde.debug.js'))
        .pipe(buffer())
        .pipe(gulp.dest('src/main/webapp/js/simplemde/debug/'));
});

gulp.task('simplemde-browserify', function () {
    return simplemdeTaskBrowserify({standalone: 'SimpleMDE'})
        .pipe(source('simplemde.js'))
        .pipe(buffer())
        .pipe(gulp.dest('src/main/webapp/js/simplemde/debug/'));
});

gulp.task('simplemde-scripts', gulp.series('simplemde-browserify:debug', 'simplemde-browserify', function () {
    var js_files = ['src/main/webapp/js/simplemde/debug/simplemde.js'];

    return gulp.src(js_files)
        .pipe(concat('simplemde.min.js'))
        .pipe(uglify())
        .pipe(buffer())
        .pipe(gulp.dest('src/main/webapp/js/simplemde/dist/'));
}));

gulp.task('simplemde-styles', function () {
    var css_files = [
        'node_modules/codemirror/lib/codemirror.css',
        'src/main/webapp/js/simplemde/src/css/*.css',
        'node_modules/codemirror-spell-checker/src/css/spell-checker.css'
    ];

    return gulp.src(css_files)
        .pipe(concat('simplemde.css'))
        .pipe(buffer())
        .pipe(gulp.dest('src/main/webapp/js/simplemde/debug/'))
        .pipe(cleanCss())
        .pipe(rename('simplemde.min.css'))
        .pipe(buffer())
        .pipe(gulp.dest('src/main/webapp/js/simplemde/dist/'));
});

gulp.task('simplemde', gulp.parallel('simplemde-scripts', 'simplemde-styles'));


gulp.task('clean', del.bind(null, ['dist', 'src/main/webapp/vendor']));

gulp.task('sass', function () {
    return gulp.src('src/main/webapp/templates/default/scss/**/*.scss')
        .pipe(plumber())
        .pipe(sass.sync({
            outputStyle: 'expanded',
            precision: 10,
            includePaths: ['.']
        }))
        .pipe(autoprefixer({browsers: ['> 1%', 'last 2 versions', 'Firefox ESR']}))
        .pipe(gulp.dest('src/main/webapp/templates/default/css/'))
        .pipe(browserSync.stream());
    // .pipe(browserSync.reload({
    //     stream: true
    // }));
});

gulp.task('watch', gulp.series('sass', function (done) {
    browserSync.init({proxy: 'localhost'});
    gulp.watch('src/main/webapp/templates/default/scss/**/*.scss', gulp.parallel('sass'));
    // gulp.watch('src/main/webapp/templates/default/js/**/*.js', browserSync.reload);
    // gulp.watch('src/main/webapp/templates/**/*.html', browserSync.reload);
    gulp.watch('src/main/webapp/templates/default/js/**/*.js').on('change', browserSync.reload);
    gulp.watch('src/main/webapp/templates/**/*.html').on('change', browserSync.reload);
    done();
}));

gulp.task('copy', function (done) {
    gulp.src('node_modules/jquery/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/jquery/dist/'));
    gulp.src('node_modules/popper.js/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/popper.js/dist'));
    // gulp.src('node_modules/tether/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/tether/'));
    gulp.src('node_modules/bootstrap/{dist,scss}/**/*').pipe(gulp.dest('src/main/webapp/vendor/bootstrap/'));
    // gulp.src('node_modules/font-awesome/{css,fonts,scss}/*').pipe(gulp.dest('src/main/webapp/vendor/font-awesome/'));
    gulp.src('node_modules/@fortawesome/fontawesome-free/{css,less,scss,webfonts}/*').pipe(gulp.dest('src/main/webapp/vendor/fontawesome-free/'));
    // gulp.src('node_modules/@fortawesome/fontawesome-free/css/*').pipe(concat('fontawesome-all.min.css')).pipe(cleanCss()).pipe(gulp.dest('src/main/webapp/vendor/fontawesome-free/css/'));
    // gulp.src('node_modules/circular-json/build/**/*').pipe(gulp.dest('src/main/webapp/vendor/circular-json/build/'));
    gulp.src('node_modules/github-markdown-css/github-markdown.css').pipe(cleanCss()).pipe(gulp.dest('src/main/webapp/vendor/github-markdown-css/'));
    // gulp.src('node_modules/bootstrap-datepicker/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/bootstrap-datepicker/dist/'));
    gulp.src('node_modules/flatpickr/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/flatpickr/dist/'));
    gulp.src('node_modules/dayjs/{dayjs.min.js,locale/*}').pipe(gulp.dest('src/main/webapp/vendor/dayjs/'));
    gulp.src('node_modules/jquery-form/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/jquery-form/dist/'));
    gulp.src('node_modules/jquery-validation/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/jquery-validation/dist/'));
    gulp.src('node_modules/jquery-confirm/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/jquery-confirm/dist/'));
    gulp.src('node_modules/js-cookie/src/*').pipe(rename({suffix: '.min'})).pipe(uglify()).pipe(gulp.dest('src/main/webapp/vendor/js-cookie/dist/'));
    gulp.src('node_modules/selectize.js/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/selectize.js/dist/'));
    gulp.src('node_modules/codemirror/{addon,keymap,lib,mode,theme}/**/*.js').pipe(uglify()).pipe(gulp.dest('src/main/webapp/vendor/codemirror/'));
    gulp.src('node_modules/codemirror/{addon,keymap,lib,mode,theme}/**/*.css').pipe(cleanCss()).pipe(gulp.dest('src/main/webapp/vendor/codemirror/'));
    gulp.src('node_modules/marked/lib/*').pipe(uglify()).pipe(gulp.dest('src/main/webapp/vendor/marked/lib/'));
    // gulp.src('node_modules/marked/lib/*').pipe(rename({suffix: '.min'})).pipe(uglify()).pipe(gulp.dest('src/main/webapp/vendor/marked/lib/'));
    gulp.src('node_modules/photoswipe/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/photoswipe/dist/'));
    gulp.src('node_modules/blueimp-file-upload/{css,img,js}/**/*').pipe(gulp.dest('src/main/webapp/vendor/blueimp-file-upload/'));
    gulp.src('node_modules/blueimp-file-upload/js/**/*.js').pipe(rename({suffix: '.min'})).pipe(uglify()).pipe(gulp.dest('src/main/webapp/vendor/blueimp-file-upload/js/'));
    gulp.src('node_modules/cropperjs/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/cropperjs/dist/'));
    gulp.src('node_modules/jquery-cropper/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/jquery-cropper/dist/'));
    gulp.src('node_modules/stomp-websocket/lib/**/*').pipe(gulp.dest('src/main/webapp/vendor/stomp-websocket/lib/'));
    gulp.src('node_modules/sockjs-client/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/sockjs-client/dist/'));
    gulp.src('node_modules/es6-promise/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/es6-promise/dist/'));
    gulp.src('node_modules/promise-polyfill/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/promise-polyfill/dist/'));
    gulp.src('node_modules/whatwg-fetch/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/whatwg-fetch/dist/'));
    gulp.src('node_modules/whatwg-fetch/dist/fetch.umd.js').pipe(rename({suffix: '.min'})).pipe(uglify()).pipe(gulp.dest('src/main/webapp/vendor/whatwg-fetch/dist/'));
    gulp.src('node_modules/qs/dist/qs.js').pipe(gulp.dest('src/main/webapp/vendor/qs/dist/'));
    gulp.src('node_modules/qs/dist/qs.js').pipe(rename({suffix: '.min'})).pipe(uglify()).pipe(gulp.dest('src/main/webapp/vendor/qs/dist/'));
    gulp.src('node_modules/jquery-serializejson/{jquery.serializejson,jquery.serializejson.min}.js').pipe(gulp.dest('src/main/webapp/vendor/jquery-serializejson/'));
    gulp.src('node_modules/lodash/{lodash,lodash.min}.js').pipe(gulp.dest('src/main/webapp/vendor/lodash/'));
    // gulp.src('node_modules/immutable/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/immutable/dist/'));

    gulp.src('node_modules/vue/dist/**/*').pipe(gulp.dest('src/main/webapp/vendor/vue/dist/'));
    done();
});

// gulp.task('default', function () {
//     // runSequence('clean', 'copy', 'sass');
//     runSequence('clean', 'copy');
// });
gulp.task('default', gulp.series('clean', 'copy'));