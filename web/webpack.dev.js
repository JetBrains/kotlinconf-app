var webpack = require("webpack");
var merge = require("webpack-merge");
var path = require("path");

var kotlinPath = path.resolve(__dirname, "build/classes/main");
module.exports = merge(require("./webpack.common.js"), {
    devtool: "inline-source-map",
    resolve: {
        modules: [path.resolve(kotlinPath, "dependencies/")]
    },
    devServer: {
        contentBase: "./src/main/web/",
        port: 9000,
        hot: true,
        proxy: [
            {
                context: ["/all", "/sessions"],
                target: "http://localhost:8080",
                ws: true
            }
        ]
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin()
    ]
});