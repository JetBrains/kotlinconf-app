var path = require("path");

module.exports = {
    entry: path.resolve(__dirname, "src/main/js/index.js"),
    output: {
        path: path.resolve(__dirname, "build/web"),
        filename: "bundle.js"
    },
    resolve: {
        modules: [path.resolve(__dirname, "node_modules"), path.resolve(__dirname, "build/classes/kotlin/main/min/")]
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                use: ["source-map-loader"],
                enforce: "pre"
            }
        ]
    }
};