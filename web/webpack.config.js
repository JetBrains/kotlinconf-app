var webpack = require("webpack");
var merge = require("webpack-merge");
var common = require("./webpack.common.js");

module.exports = merge(common, {
    devtool: "source-map",
    plugins: [
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: JSON.stringify('production')
            }
        }),
        new webpack.optimize.UglifyJsPlugin({
            sourceMap: true
        })
    ]
});