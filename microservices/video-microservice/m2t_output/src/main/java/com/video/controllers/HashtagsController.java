// protected region packageDefinition on begin
package todo
// protected region packageDefinition end

@Controller("/hashtags")
public class HashtagsController {

	// protected region classVariables on begin
	// Declare variables here...
	// protected region classVariables end

	@Get("TODO")
	public Iterable<Hashtag> list() {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Post("TODO")
	public HttpResonse<Void> add(HashtagDTO hashtagDetails) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Hashtag getHashtag(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Delete("TODO")
	public HttpResonse<Void> deleteHashtag(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Post("TODO")
	public Iterable<Video> getVideos(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

}