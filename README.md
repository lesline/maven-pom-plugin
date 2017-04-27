mvn com.juvenxu.mvnbook:maven-loc-plugin:0.0.1-SNAPSHOT:count

mvn com.eloancn:maven-pom-plugin:0.0.1-SNAPSHOT:release



<plugin>
	<groupId>com.juvenxu.mvnbook</groupId>
	<artifactId>maven-loc-plugin</artifactId>
	<version>0.0.1</version>
	<executions>
		<execution>
			<goals>
				<goal>count</goal>
			</goals>
		</execution>
	</executions>
</plugin>
<plugin>
	<groupId>com.eloancn</groupId>
	<artifactId>maven-pom-plugin</artifactId>
	<version>0.0.1</version>
	<executions>
		<execution>
			<goals>
				<goal>release</goal>
			</goals>
		</execution>
	</executions>
</plugin>