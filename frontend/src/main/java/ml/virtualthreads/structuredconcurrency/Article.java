package ml.virtualthreads.structuredconcurrency;

class Article {
    Integer articleId;
    String title;

    Article() {
        articleId = null;
        title = null;
    }

    Article(Integer articleId, String title) {
        this.articleId = articleId;
        this.title = title;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public String getTitle() {
        return title;
    }
}
