'use strict';

var path   = require('path')
  , yeoman = require('yeoman-generator');

module.exports = yeoman.generators.Base.extend({


  initializing: function () {
    this.props = this.config.getAll();
  },

  writing: function () {
    var performerPath = (this.props.performerPackage || '').replace(/\./g, '/');
    var modelPath = (this.props.modelPackage || '').replace(/\./g, '/');

    this.props.performerPath = performerPath;
    this.props.modelPath = modelPath;

    this.template(path.join('src/main/java','Activity.java'), path.join(this.props.performerModule, 'src/main/java', modelPath, this.props.component + 'Activity.java'));
    this.template(path.join('src/main/java','ObjectFactory.java'), path.join(this.props.performerModule, 'src/main/java', modelPath, 'ObjectFactory.java'));
    this.template(path.join('src/main/java','ActivityPerformer.java'), path.join(this.props.performerModule, 'src/main/java', performerPath, this.props.component +'ActivityPerformer.java'));
    this.template(path.join('src/main/java','ActivityPerformerFactory.java'), path.join(this.props.performerModule, 'src/main/java', performerPath, this.props.component +'ActivityPerformerFactory.java'));
    
    this.directory('build/lib', path.join(this.props.performerModule, 'build/lib'));
    this.template(path.join('build', 'build.xml'), path.join(this.props.performerModule, 'build', 'build.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('build', 'project.properties'), path.join(this.props.performerModule, 'build', 'project.properties'));
    this.template(path.join('build', 'build.properties'), path.join(this.props.performerModule, 'build.properties'));
    this.template('pom.xml', path.join(this.props.performerModule, 'pom.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g});
    
    this.template(path.join('META-INF/spring', 'activity-osgi.xml'), path.join(this.props.performerModule,'META-INF/spring', this.props.component.toLowerCase()  +'-activity-osgi.xml'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('META-INF/resources', 'activity.xsd'), path.join(this.props.performerModule,'META-INF/resources', 'activity.xsd'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('META-INF/resources', 'activity.xjb'), path.join(this.props.performerModule,'META-INF/resources', 'activity.xjb'), null, { 'interpolate': /<%=([\s\S]+?)%>/g });
    this.template(path.join('META-INF', 'MANIFEST.INF'), path.join(this.props.performerModule, 'META-INF', 'MANIFEST.MF'));

    this.template(path.join('OSGI-INF', 'message.properties'), path.join(this.props.performerModule, 'OSGI-INF/l10n', 'message.properties'));

  },

  end: function(){
    this.config.set(this.props)
  }
});

