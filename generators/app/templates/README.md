# <%= props.title %><% if (props.description) { %>

<%= props.description %><% } %>

## Requirements

- [Java SE](http://www.oracle.com/technetwork/java/javase/overview)
- [Ant](http://ant.apache.org)
- [Maven] (http://maven.apache.org)

Create symlink to the `lib` folder under your Policy Manager installation directory.

```bash
ln -s $AKANA_HOME/sm72/lib/ lib
```

To generate an Eclipse project

```bash
mvn eclipse:eclipse
```

To build

```bash
cd build
ant
```

Build artifact: OSGi Repository
Artifact: /build/<%= props.component.toLowerCase() %>-<%= props.bundleVersion %>.zip

To deploy the build

Take the built ./build/dist/<%= props.component.toLowerCase() %>-<%= props.bundleVersion %>.zip file and unzip it under the `$AKANA_HOME/sm72/lib` folder. Use Admin console to install the 'Plugin'

---

Copyright &copy; <%= new Date().getFullYear() %> <%= props.author %>.
