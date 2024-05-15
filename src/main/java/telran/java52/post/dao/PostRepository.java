package telran.java52.post.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java52.post.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {
	
	Iterable<Post> findByAuthor(String author);
    Iterable<Post> findByTagsIn(List<String> tags);
    Iterable<Post> findByDateCreatedBetween(LocalDate startDate, LocalDate endDate);
}
