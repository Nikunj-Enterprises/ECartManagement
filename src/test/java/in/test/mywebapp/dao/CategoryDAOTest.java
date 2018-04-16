package in.test.mywebapp.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import in.test.mywebapp.common.ApplicationConstants;
import in.test.mywebapp.exception.AlreadyExistException;
import in.test.mywebapp.model.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;



public class CategoryDAOTest{
	
    private CategoryDAO categoryDao;
    @InjectMocks
    private Category category ;
    private static MongoOperations mongoTemplate = null;

    private static MongodExecutable mongodExe;
    private MongodProcess mongod;
    private Mongo mongo;

    private static MongodStarter starter = MongodStarter.getDefaultInstance();

    private static String bindIp = "localhost";
    private  static int port = 12345;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .build();
        mongodExe = starter.prepare(mongodConfig);
        MongodProcess mongod = mongodExe.start();
        MongoClient mongo = new MongoClient(bindIp, port);
        MongoDbFactory factory = new SimpleMongoDbFactory(mongo,"test_embedded");
        mongoTemplate = new MongoTemplate(factory);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // drop all created collections, indexes and sequence in test db
        if (mongodExe != null)
            mongodExe.stop();
    }


    @Before
    public void setUp() throws Exception {
        categoryDao= new CategoryDAO(mongoTemplate);
        mongoTemplate.getCollection("categories").remove(new BasicDBObject());
        mongoTemplate.getCollection("category_properties").remove(new BasicDBObject());
        mongoTemplate.getCollection("category_images").remove(new BasicDBObject());
        
        DBObject obj = new BasicDBObject();
        obj.put("id", new ObjectId(ApplicationConstants.TOP_MOST_CATEGORY_ID));
        obj.put("categoryName", ApplicationConstants.TOP_MOST_CATEGORY_NAME);
        obj.put("isItem", false);
        mongoTemplate.getCollection("categories").insert(obj);
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection("categories");
        mongoTemplate.dropCollection("category_properties");
        mongoTemplate.dropCollection("category_images");
    }

    @Test
    public void testCreateCategory(){
    	category.setCategoryName("test");
    	category.setParentCategoryName(ApplicationConstants.TOP_MOST_CATEGORY_NAME);
    	int i = categoryDao.createCategory(category);
    	assertEquals(i,1);
    	
    	Category aCategory = categoryDao.findCategory("test", ApplicationConstants.TOP_MOST_CATEGORY_NAME);
    	assertEquals(aCategory.getCategoryName(),"test");
    	assertEquals(aCategory.getParentCategoryName(), ApplicationConstants.TOP_MOST_CATEGORY_NAME);
    	assertFalse(aCategory.isItem());
    }
    
    @Test
    public void testCreateCategory_passingNoParentCategory(){
    	category.setCategoryName("test");
    	category.setParentCategoryName(null);
    	int i = categoryDao.createCategory(category);
    	assertEquals(i,1);
    	
    	Category aCategory = categoryDao.findCategory("test", ApplicationConstants.TOP_MOST_CATEGORY_NAME);
    	assertEquals(aCategory.getCategoryName(),"test");
    	assertEquals(aCategory.getParentCategoryName(), ApplicationConstants.TOP_MOST_CATEGORY_NAME);
    	assertFalse(aCategory.isItem());
    }
    
    @Test(expected=AlreadyExistException.class)
    public void testCreateCategory_Duplicate(){
    	category.setCategoryName("test");
    	category.setParentCategoryName(null);
    	int i = categoryDao.createCategory(category);
    	assertEquals(i,1);
    	
    	categoryDao.createCategory(category);
    }
    
    @Test
    public void testFindCategory() {
    	
    }
    
    @Test
    public void testFindCategory_NonExisting() {
    	
    }
    
    @Test
    public void testFindSubCategories() {
    	
    }
    
    @Test
    public void testFindSubCategories_NoSubCategory() {
    	
    }
    
    @Test
    public void testFindSubCategories_NonExisting() {
    	
    }
}