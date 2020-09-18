const gulp = require("gulp");
const sourcemaps = require("gulp-sourcemaps");
const ts = require("gulp-typescript");
const replace = require("gulp-replace");

const locations = {
    tsconfig: "client/tsconfig.json",
    html: "client/src/**/*.html",
    output: "server/build/client_static"
}

gulp.task("build", gulp.parallel(buildTs, copyHtml));

function buildTs()
{
    const clientProj = ts.createProject(locations.tsconfig);

    return clientProj.src()
        .pipe(sourcemaps.init())
        .pipe(clientProj()).js
        .pipe(replace(/(import .* from\s+['"])(.*)(?=['"])/g, "$1$2.js"))
        .pipe(sourcemaps.write())
        .pipe(gulp.dest(locations.output));
}

function copyHtml()
{
	return gulp.src(locations.html)
		.pipe(gulp.dest(locations.output));
}