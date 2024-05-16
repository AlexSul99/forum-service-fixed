package telran.java52.post.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import telran.java52.post.dao.PostRepository;
import telran.java52.post.dto.DatePeriodDto;
import telran.java52.post.dto.NewCommentDto;
import telran.java52.post.dto.NewPostDto;
import telran.java52.post.dto.PostDto;
import telran.java52.post.dto.exception.PostNotFoundException;
import telran.java52.post.model.Comment;
import telran.java52.post.model.Post;



@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	
	final PostRepository postRepository;
	final ModelMapper modelMapper;
	
	@Override
	public PostDto addNewPost(String author, NewPostDto newPostDto) {
		Post post = modelMapper.map(newPostDto, Post.class);
		post.setAuthor(author);
		post = postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto findPostById(String id) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		return modelMapper.map(post,PostDto.class);
	}

	@Override
	public PostDto removePost(String id) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		postRepository.deleteById(id);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto updatePost(String id, NewPostDto newPostDto) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		if(newPostDto.getTitle()!=null) {
			post.setTitle(newPostDto.getTitle());
		}
		if(newPostDto.getContent()!=null) {
			post.setContent(newPostDto.getContent());
		}
		Set<String> tags = newPostDto.getTags();
		if(tags!=null) {
			tags.forEach(post::addTag);
		}
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		Comment comment = new Comment(author,newCommentDto.getMessage());
		post.addComment(comment);
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public void addLike(String id) {
		Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
		post.addLike();
		postRepository.save(post);

	}

	 @Override
	    public Iterable<PostDto> findPostsByAuthor(String author) {
	        Iterable<Post> posts = postRepository.findByAuthor(author);
	        return mapPostsToDto(posts);
	    }

	 @Override
	    public Iterable<PostDto> findPostsByTags(List<String> tags) {
	        Iterable<Post> posts = postRepository.findByTagsIn(tags);
	        return mapPostsToDto(posts);
	    }

	 @Override
	 public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
	     LocalDate startDate = datePeriodDto.getDateFrom();
	     LocalDate endDate = datePeriodDto.getDateTo();
	     Iterable<Post> posts = postRepository.findByDateCreatedBetween(startDate, endDate);
	     return StreamSupport.stream(posts.spliterator(), false)
	                         .map(post -> modelMapper.map(post, PostDto.class))
	                         .collect(Collectors.toList());
	 }
	
	
	 public Iterable<PostDto> mapPostsToDto(Iterable<Post> posts) {
		    return StreamSupport.stream(posts.spliterator(), false)
		                        .map(post -> modelMapper.map(post, PostDto.class))
		                        .collect(Collectors.toList());
		}

}
