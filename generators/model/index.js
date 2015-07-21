'use strict';

var path   = require('path')
  , yeoman = require('yeoman-generator');

module.exports = yeoman.generators.Base.extend({


  initializing: function () {
    this.props = this.config.getAll();
  },

  writing: function () {
    
    var modelPath = (this.props.modelPackage || '').replace(/\./g, '/');
    this.props.modelPath = modelPath;

    this.template(path.join('src/main/java','Activity.java'), path.join(this.props.modelModule, 'src/main/java', modelPath, this.props.component + 'Activity.java'));
    this.template(path.join('src/main/java','ObjectFactory.java'), path.join(this.props.modelModule, 'src/main/java', modelPath, 'ObjectFactory.java'));
    this.template(path.join('src/main/java','package-info.java'), path.join(this.props.modelModule, 'src/main/java', modelPath, 'package-info.java'));
    this.log(this.props.bundle_version)
    
    this.directory('build/lib', path.join(this.props.modelModule, 'build/lib'));
    this.template(path.join('build', 'build.xml'), path.join(this.props.modelModule, 'build', 'build.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('build', 'project.properties'), path.join(this.props.modelModule, 'build', 'project.properties'), null, { 'interpolate': /<%=([\s\S]+?)%>/g});
    //this.template(path.join('build', 'build.properties'), path.join(this.props.modelModule, 'build.properties'));
    this.template('pom.xml', path.join(this.props.modelModule, 'pom.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g});
    
    this.template(path.join('META-INF/resources', 'activity.xsd'), path.join(this.props.modelModule,'META-INF/resources', 'activity.xsd'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('META-INF/resources', 'activity.xjb'), path.join(this.props.modelModule,'META-INF/resources', 'activity.xjb'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('META-INF', 'MANIFEST.INF'), path.join(this.props.modelModule, 'META-INF', 'MANIFEST.MF'));

  },

  end: function(){
    this.config.set(this.props)
  }
});

