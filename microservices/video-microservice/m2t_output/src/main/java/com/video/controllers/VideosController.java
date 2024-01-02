// protected region packageDefinition on begin
package todo
// protected region packageDefinition end

@Controller("/videos")
public class VideosController {

	// protected region classVariables on begin
	// Declare variables here...
	// protected region classVariables end

	@Post("TODO")
	public HttpResonse<Void> add(VideoDTO videoDetails) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Video getVideo(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Put("TODO")
	public HttpResonse<Void> updateVideo(VideoDTO video, long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Delete("TODO")
	public HttpResonse<Void> deleteVideo(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<Hashtag> getHashtags(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Put("TODO")
	public HttpResonse<String> addHashtag(long videoId, long hashtagId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<User> getViewers(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Put("TODO")
	public HttpResonse<String> addViewer(long videoId, long userId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Delete("TODO")
	public HttpResonse<String> removeViewer(long videoId, long userId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<User> getLikers(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<User> getDislikers(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Put("TODO")
	public HttpResonse<String> addLiker(long videoId, long userId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Put("TODO")
	public HttpResonse<String> addDisliker(long videoId, long userId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Delete("TODO")
	public HttpResonse<String> removeDisliker(long videoId, long userId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Delete("TODO")
	public HttpResonse<String> removeLiker(long videoId, long userId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<Video> list() {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

}