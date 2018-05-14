# Spring事务注解备忘录

> https://www.ibm.com/developerworks/cn/java/j-master-spring-transactional-use/index.html

## 注解只能打在public方法上
```
# @see org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
	public AnnotationTransactionAttributeSource() {
		this(true);
	}

	public AnnotationTransactionAttributeSource(boolean publicMethodsOnly) {
		this.publicMethodsOnly = publicMethodsOnly;
		this.annotationParsers = new LinkedHashSet<TransactionAnnotationParser>(2);
		this.annotationParsers.add(new SpringTransactionAnnotationParser());
		if (jta12Present) {
			this.annotationParsers.add(new JtaTransactionAnnotationParser());
		}
		if (ejb3Present) {
			this.annotationParsers.add(new Ejb3TransactionAnnotationParser());
		}
	}
	
	
# @see org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource
	protected TransactionAttribute computeTransactionAttribute(Method method, Class<?> targetClass) {
		// Don't allow no-public methods as required.
		if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
			return null;
		}
		
		...
	
	}
```

## rollback 默认只对RuntimeException和Error生效
```
# org.springframework.transaction.interceptor.DefaultTransactionAttribute
	@Override
	public boolean rollbackOn(Throwable ex) {
		return (ex instanceof RuntimeException || ex instanceof Error);
	}


# org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
	@Override
	public boolean rollbackOn(Throwable ex) {
		if (logger.isTraceEnabled()) {
			logger.trace("Applying rules to determine whether transaction should rollback on " + ex);
		}

		RollbackRuleAttribute winner = null;
		int deepest = Integer.MAX_VALUE;

		if (this.rollbackRules != null) {
			for (RollbackRuleAttribute rule : this.rollbackRules) {
				int depth = rule.getDepth(ex);
				if (depth >= 0 && depth < deepest) {
					deepest = depth;
					winner = rule;
				}
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Winning rollback rule is: " + winner);
		}

		// User superclass behavior (rollback on unchecked) if no rule matches.
		if (winner == null) {
			logger.trace("No relevant rollback rule found: applying default rules");
			
			/**
			* 关键代码，如果winner为空，则调用super的rollbackOn方法，而super为org.springframework.transaction.interceptor.DefaultTransactionAttribute
			*/
			return super.rollbackOn(ex);
		}

		return !(winner instanceof NoRollbackRuleAttribute);
	}
```

## 同一个类中的无事务注解方法调用有事务注解方法配置
```
// 1. 指定 mode 为 AdviceMode.ASPECTJ
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)

// 2. 引入 aspect 包
        <!-- https://mvnrepository.com/artifact/org.springframework/spring-aspects -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>4.3.6.RELEASE</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.springframework/spring-instrument -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-instrument</artifactId>
            <version>4.3.6.RELEASE</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.aspectj/aspectjrt -->
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjrt</artifactId>
            <version>1.8.9</version>
        </dependency>

// 3 和 4 选一个配置就好了：

// 3. pom文件里配置插件
    <properties>
        <jdk.version>1.8</jdk.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>aspectj-maven-plugin</artifactId>
                <version>1.9</version>
                <configuration>
                    <source>${jdk.version}</source>
                    <target>${jdk.version}</target>
                    <complianceLevel>${jdk.version}</complianceLevel>
                    <encoding>${project.build.sourceEncoding}</encoding>
                    <showWeaveInfo>true</showWeaveInfo>
                    <Xlint>ignore</Xlint>
                    <verbose>true</verbose>
                    <aspectLibraries>
                        <aspectLibrary>
                            <groupId>org.springframework</groupId>
                            <artifactId>spring-aspects</artifactId>
                        </aspectLibrary>
                    </aspectLibraries>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

// 4. java启动脚本中配置代理
-javaagent:/Users/ervin/.m2/repository/org/springframework/spring-instrument/4.3.6.RELEASE/spring-instrument-4.3.6.RELEASE.jar  -javaagent:/Users/ervin/.m2/repository/org/aspectj/aspectjweaver/1.8.9/aspectjweaver-1.8.9.jar

```
