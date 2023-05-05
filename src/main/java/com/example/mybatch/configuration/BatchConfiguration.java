package com.example.mybatch.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.mybatch.notification.JobCompletionNotificationListener;
import com.example.mybatch.processor.PersonItemProcessor;
import com.example.mybatch.schema.Person;

@Component
public class BatchConfiguration {

	@Bean
	public FlatFileItemReader<Person> reader() {
	    return new FlatFileItemReaderBuilder<Person>()
			.name("personItemReader")
			.resource(new ClassPathResource("person.csv"))
			.delimited()
			.names(new String[] { "id", "name" })
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}})
	      .build();
	}
	
	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
	    return new JdbcBatchItemWriterBuilder<Person>()
	      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
	      .sql("INSERT INTO person (id,name) VALUES (:id, :name)")
	      .dataSource(dataSource)
	      .build();
	}
	
	@Bean
	public Job importUserJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
	    return new JobBuilder("importUserJob", jobRepository)
	      .incrementer(new RunIdIncrementer())
	      .listener(listener)
	      .flow(step1)
	      .end()
	      .build();
	}

	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Person> writer) {
	    return new StepBuilder("step1", jobRepository)
	      .<Person, Person> chunk(10, transactionManager)
	      .reader(reader())
	      .processor(processor())
	      .writer(writer)
	      .build();
	}
	
	@Bean
	public PersonItemProcessor processor() {
	    return new PersonItemProcessor();
	}
}
