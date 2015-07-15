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

To deploy the build

Take the built jar file and drop it under the `$AKANA_HOME/sm72/instances/$ND_INSTANCE_NAME/deploy` folder

---

Copyright &copy; <%= new Date().getFullYear() %> <%= props.author %>.
