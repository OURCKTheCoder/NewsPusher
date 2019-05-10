#install.packages("tm")
#install.packages("gmodels")
#install.packages("class")
#install.packages("e1701")
library("tm")
library("class")
library("gmodels")
#library("e1701")
args <- commandArgs(T)
args[1]
args[2]
FILE_PATH <- args[1]
OUTPUT_FILE <- args[2]
listing <- read.csv(FILE_PATH, stringsAsFactors=FALSE, sep='\t')

ITEMS_NUM <- nrow(listing)
TRAIN_NUM <- floor(nrow(listing) * 0.9)
TEST_NUM <- TRAIN_NUM + 1
CLUSTER_NUM <- floor(sqrt(ITEMS_NUM / 2)) # "Square ITEMS_NUM / 2"




##
# ---------------------------- BEGINS ---------------------------- 
##
loadFile <- function(file) {
	listing <- read.csv(file, stringsAsFactors=FALSE, sep='\t')
	listing$channel_id <- factor(listing$channel_id)
	listing$tags <- factor(listing$tags)
	listing_corpus <- VCorpus(VectorSource(listing$title))
	listing_dtm <- DocumentTermMatrix(listing_corpus,control=list(wordLengths=c(0,Inf)))
#	convert <- function(x) { x <- ifelse( x > 0, "Yes", "No") }
#	listing_all <- apply(listing_dtm, MARGIN = 2, convert)
#	listing_all
	listing_dtm
}
	# Calculating Doc-Words MTX
listing_dtm <- loadFile(FILE_PATH)
copy <- function(x) { x <- x }

	# KNN Classifying
listing_all_knn <- as.data.frame(apply(listing_dtm, MARGIN = 2, copy))

	# Cutting data for training & testing
listing_train_knn <- listing_all_knn[1:TRAIN_NUM, ]
listing_test_knn <- listing_all_knn[TEST_NUM:ITEMS_NUM, ]
listing_train_labels <- listing[1:TRAIN_NUM, ]$channel_id
listing_test_labels <- listing[TEST_NUM:ITEMS_NUM, ]$channel_id

listing_pridict <- knn(train = listing_train_knn, test = listing_test_knn, cl = listing_train_labels, k = 3)

	# FORTEST: Calculating ConfusionMTX
CrossTable(listing_pridict, listing_test_labels, prop.chisq = FALSE, prop.t = FALSE, dnn = c('预测值','实际值'))

	# KMEANS Clustering
listing_clustered <- kmeans(listing_all_knn, CLUSTER_NUM)
listing_clustered_test <- as.data.frame(listing_clustered$cluster)
listing_clustered$size # A glance at clusered data.
write.table(listing_clustered_test, OUTPUT_FILE, sep = "\t")

