'use strict';

var path   = require('path')
  , yeoman = require('yeoman-generator');

module.exports = yeoman.generators.Base.extend({


  initializing: function () {
    this.props = this.config.getAll();
  },

  writing: function () {
    var rendererPath = (this.props.rendererPackage || '').replace(/\./g, '/');
    var validatorPath = (this.props.validatorPackage || '').replace(/\./g, '/');    

    this.template(path.join('src/main/java','ActivityDetailsBean.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, this.props.component + 'ActivityDetailsBean.java'));
    this.template(path.join('src/main/java','ActivityRenderer.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, this.props.component + 'ActivityRenderer.java'));
    this.template(path.join('src/main/java','BaseActivityDetailsBean.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, 'BaseActivityDetailsBean.java'));
    this.template(path.join('src/main/java','BaseActivityRenderer.java'), path.join(this.props.rendererModule, 'src/main/java', rendererPath, 'BaseActivityRenderer.java'));
    this.template(path.join('src/main/java','ActivityValidator.java'), path.join(this.props.rendererModule, 'src/main/java', validatorPath, this.props.component + 'ActivityValidator.java'));

    this.directory('src/main/web/WEB-INF/tlds', path.join(this.props.rendererModule, 'src/main/webapps/WEB-INF/tlds'));
    this.template(path.join('src/main/web/WEB-INF', 'web.xml'), path.join(this.props.rendererModule, 'src/main/webapps/WEB-INF', 'web.xml'));
    this.template(path.join('src/main/web', 'activity_details.jsp'), path.join(this.props.rendererModule, 'src/main/webapps', 'activity_details.jsp'), null, {
                                                                        'escape': /<@-([\s\S]+?)@>/g,
                                                                        'evaluate': /<@([\s\S]+?)@>/g,
                                                                        'interpolate': /<@=([\s\S]+?)@>/g});
    this.template(path.join('build', 'build.xml'), path.join(this.props.rendererModule, 'build', 'build.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('build', 'project.properties'), path.join(this.props.rendererModule, 'build', 'project.properties'), null, { 'interpolate': /<%=([\s\S]+?)%>/g});
    this.template(path.join('build', 'build.properties'), path.join(this.props.rendererModule, 'build.properties'));
    this.template('pom.xml', path.join(this.props.rendererModule, 'pom.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g});

    this.directory('META-INF/resources', path.join(this.props.rendererModule, 'META-INF/resources'));
    this.template(path.join('META-INF/spring', 'activity-ui-osgi.xml'), path.join(this.props.rendererModule,'META-INF/spring', this.props.component.toLowerCase()  +'-activity-ui-osgi.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('META-INF/spring', 'activity-ui-servlet.xml'), path.join(this.props.rendererModule,'META-INF/spring', this.props.component.toLowerCase()  +'-activity-ui-servlet.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('META-INF', 'MANIFEST.INF'), path.join(this.props.rendererModule, 'META-INF', 'MANIFEST.MF'));

    this.template(path.join('OSGI-INF', 'message.properties'), path.join(this.props.rendererModule, 'OSGI-INF/l10n', 'message.properties'));

  },

  end: function(){
    this.config.set(this.props)
  }
});

