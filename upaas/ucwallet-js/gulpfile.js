// 引入需要的模块
var gulp = require('gulp');
var del = require('del');
var runSequence = require('run-sequence');
var developServer = require('gulp-develop-server');
var notify = require('gulp-notify');

gulp.task('default', function(callback){
    return runSequence(['clean'], ['copyFiles'], ['serve', 'watch'], callback);
});

gulp.task('clean', function(callback){
    return del('./dist/', callback);
});

gulp.task('copyFiles', function(){
    return gulp.src('./src/**/**/*')
        .pipe(gulp.dest('./dist/'));
});

gulp.task('serve', function(){
    return developServer.listen({
        path : './dist/index.js'
    });
});

gulp.task('watch', function(){
    return gulp.watch('./src/**/**/*', ['reload']);
});

gulp.task('reload', function(callback){
    return runSequence(['copyFiles'], ['reload-node'], callback);
});

gulp.task('reload-node', function(){
    return developServer.restart();
    gulp.src('./dist/index.js')
        .pipe(notify('Server restart ...'));
});