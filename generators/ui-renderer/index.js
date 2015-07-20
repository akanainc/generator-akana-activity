'use strict';

var path   = require('path')
  , yeoman = require('yeoman-generator');

module.exports = yeoman.generators.Base.extend({


  initializing: function () {
    this.props = this.config.getAll();
    this.log('in renderer:init')
  },

  writing: function () {
    this.log('in renderer:writing')
    var rendererPath = (this.props.rendererPackage || '').replace(/\./g, '/');
    var validatorPath = (this.props.validatorPackage || '').replace(/\./g, '/');    

    this.template(path.join('src/main/java','ActivityDetailsBean.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, this.props.component + 'ActivityDetailsBean.java'));
    this.template(path.join('src/main/java','ActivityRenderer.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, this.props.component + 'ActivityRenderer.java'));
    this.template(path.join('src/main/java','BaseActivityDetailsBean.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, 'BaseActivityDetailsBean.java'));
    this.template(path.join('src/main/java','BaseActivityRenderer.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, 'BaseActivityRenderer.java'));
    this.template(path.join('src/main/java','ActivityValidator.java'), path.join(this.props.rendererModule, 'src/main/java', validatorPath, this.props.component + 'ActivityValidator.java'));


    this.template(path.join('build', 'build.xml'), path.join(this.props.rendererModule, 'build', 'build.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('build', 'project.properties'), path.join(this.props.rendererModule, 'build', 'project.properties'));
    this.template(path.join('build', 'build.properties'), path.join(this.props.rendererModule, 'build.properties'));
    this.template('pom.xml', path.join(this.props.rendererModule, 'pom.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g});

  },

  end: function(){
    this.config.set(this.props)
  }
});

