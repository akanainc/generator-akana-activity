'use strict';
var yeoman = require('yeoman-generator');
var chalk = require('chalk');
var yosay = require('yosay'),
    validator = require('validator'),
    superb    = require('superb'),
    superheroes  = require('superheroes'),
    path      = require('path');

module.exports = yeoman.generators.Base.extend({
  prompting: function () {
    var done = this.async();

    // Have Yeoman greet the user.
    this.log(yosay(
      'Welcome to the ' + superb() +  chalk.red(' Akana Process Activity') +' generator!'
    ));

    var prompts = [{
    var prompts = [{
       type: 'input'
    , name: 'title'
    , message: 'Name your project'
    , default: this.config.get('title')
    , validate: function (input) {
        return input ? true : false;
      }
    }, {
      type: 'input'
    , name: 'description'
    , message: 'What\'s your project about? (optional)'
    , default: this.config.get('description') || null
    }, {
      type: 'input'
    , name: 'author'
    , message: 'What\'s your name?'
    , default: this.config.get('author')
    , validate: function (input) {
        return input ? true : false;
      }
    }, {
      type: 'input'
    , name: 'email'
    , message: 'What\'s your email?'
    , default: this.config.get('email')
    , validate: function (input) {
        return validator.isEmail(input);
      }
    }, {
      type: 'input'
    , name: 'namespace'
    , message: 'Name your Java package namespace (e.g. com.akana.activity.transform)'
    , default: this.config.get('namespace')
    },{
       type: 'input'
    , name: 'component'
    , message: 'Name your Java class prefix (TitleCase - Alphanumeric)'
    , default: this.config.get('component')
    , validate: function (input) {
        return validator.isAlphanumeric(input);
      }
    }];

    this.prompt(prompts, function (props) {
      this.props = props;
      // To access props later use this.props.someOption;
      this.props.rendererPackage = this.props.namespace + '.ui';
      this.props.validatorPackage = this.props.rendererPackage + '.validator';
      this.props.performerPackage = this.props.namespace + '.performer';
      this.props.commonPackage = this.props.namespace + '.common';
      this.props.modelPackage = this.props.commonPackage + '.model';
      this.config.set(this.props);
      done();
    }.bind(this));
  },

  writing: {
    app: function () {

    },

    projectfiles: function () {
      this.fs.copy(
        this.templatePath('editorconfig'),
        this.destinationPath('.editorconfig')
      );
      this.fs.copy(
        this.templatePath('gitignore'),
        this.destinationPath('.gitignore')
      );
    },

    readme: function () {
      this.template('README.md');
    },

    renderer: function(){

    }
  },

  install: function () {
    this.installDependencies();
  }
});
