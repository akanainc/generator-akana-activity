'use strict';

var path   = require('path')
  , yeoman = require('yeoman-generator');

var RendererGenerator = yeoman.generators.Base.extend({

  constructor: function(){
    yeoman.generators.Base.apply(this, arguments);
    this.props = this.config.getAll();
  },

  initializing: function () {

  },

  writing: function () {
    var rendererPath = (this.props.rendererPackage || '').replace(/\./g, '/');
    var validatorPath = (this.props.validatorPackage || '').replace(/\./g, '/');    

    this.template('ActivityDetailsBean.java', path.join('src/main/java', rendererPath, this.props.component + 'ActivityDetailsBean.java'));
    this.template('ActivityRenderer.java', path.join('src/main/java', rendererPath, this.props.component + 'ActivityRenderer.java'));
    this.template('BaseActivityDetailsBean.java', path.join('src/main/java', rendererPath, 'BaseActivityDetailsBean.java'));
    this.template('BaseActivityRenderer.java', path.join('src/main/java', rendererPath, 'BaseActivityRenderer.java'));
    this.template('ActivityValidator.java', path.join('src/main/java', validatorPath, this.props.component + 'ActivityValidator.java'));

  },

  end: function(){
    this.config.set(this.props)
  }
});

module.exports = RendererGenerator;
