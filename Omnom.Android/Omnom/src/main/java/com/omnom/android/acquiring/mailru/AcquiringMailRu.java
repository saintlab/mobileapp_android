package com.omnom.android.acquiring.mailru;

import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.BuildConfig;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.TreeSet;

import hugo.weaving.DebugLog;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringMailRu implements Acquiring {
	private static final String TAG = AcquiringMailRu.class.getSimpleName();

	private static String encryptPassword(String password) {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(password.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sha1;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for(byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private final AcquiringServiceMailRu mAcquiringService;
	private Context mContext;

	// TODO: Implement
	//	-(void)checkRegisterForResponse:(id)
	//	response withCompletion
	//	:(void(^)(
	//	id response, NSString
	//	*cardId))completionBlock
	//
	//	{
	//
	//		__weak typeof (self) weakSelf = self;
	//		NSString * url = response[ @ "url"];
	//		[self GET:url parameters:nil success:^(AFHTTPRequestOperation * operation, id responseObject){
	//
	//		NSString * status = responseObject[ @ "status"];
	//		if([status isEqualToString:@ "OK_CONTINUE"]){
	//			dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t) (1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^ {
	//					[weakSelf checkRegisterForResponse:
	//			response withCompletion:completionBlock];
	//			});
	//		}
	//		else{
	//
	//			completionBlock(responseObject, response[ @ "card_id"]);
	//
	//		}
	//
	//	} failure:^(AFHTTPRequestOperation * operation, NSError * error){
	//
	//		completionBlock([operation omn_errorResponse],response[ @ "card_id"]);
	//
	//	}];
	//
	//	}
	//
	//	-(void)cardVerify:(double)
	//	amount user_login
	//	:(NSString*)
	//	user_login card_id
	//	:(NSString*)
	//	card_id completion
	//	:(void(^)(
	//	id response
	//	))completionBlock
	//
	//	{
	//
	//		NSAssert(completionBlock != nil, @ "cardVerify completionBlock is nil");
	//
	//		NSDictionary * reqiredSignatureParams =
	//		@ {
	//		@ "merch_id":_config[ @ "OMNMailRu_merch_id"],
	//		@ "vterm_id":_config[ @ "OMNMailRu_vterm_id"],
	//		@ "user_login":user_login,
	//		@ "card_id":card_id,
	//	} ;
	//
	//		NSMutableDictionary * parameters =[reqiredSignatureParams mutableCopy];
	//
	//		parameters[ @ "signature"]=[reqiredSignatureParams omn_signature];
	//		parameters[ @ "amount"]=@(amount) ;
	//
	//		__weak typeof (self) weakSelf = self;
	//		[self POST:@ "card/verify" parameters:
	//	parameters success:^(AFHTTPRequestOperation * operation, id responseObject){
	//
	//		completionBlock(responseObject);
	//		#warning card/verify
	//		//    if (responseObject[@"error"]) {
	//		//      completionBlock(responseObject);
	//		//    }
	//		//    else {
	//		//
	//		//      NSString *url = responseObject[@"url"];
	//		//      if (NSNotFound == [url rangeOfString:@"Success=True"].location) {
	//		//        completionBlock(nil);
	//		//      }
	//		//      else {
	//		//        completionBlock(responseObject);
	//		//      }
	//		//
	//		//    }
	//
	//	} failure:^(AFHTTPRequestOperation * operation, NSError * error){
	//
	//		completionBlock([operation omn_errorResponse]);
	//
	//	}];
	//
	//	}
	//
	//	-(void)pollUrl:(NSString*)
	//	url withCompletion
	//	:(void(^)(
	//	id response
	//	))completionBlock
	//
	//	{
	//
	//		__weak typeof (self) weakSelf = self;
	//		[self GET:url parameters:nil success:^(AFHTTPRequestOperation * operation, id responseObject){
	//
	//		NSLog( @ "\npollUrl:>\n%@", responseObject);
	//
	//		NSString * status = responseObject[ @ "status"];
	//		if([status isEqualToString:@ "OK_CONTINUE"]){
	//			dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t) (1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^ {
	//					[weakSelf pollUrl:
	//			url withCompletion:completionBlock];
	//			});
	//		}
	//		else{
	//
	//			completionBlock(responseObject);
	//
	//		}
	//
	//	} failure:^(AFHTTPRequestOperation * operation, NSError * error){
	//
	//		completionBlock([operation omn_errorResponse]);
	//
	//	}];
	//
	//	}
	//
	//	-(void)payWithInfo:(OMNMailRuPaymentInfo*)
	//	paymentInfo completion
	//	:(void(^)(
	//	id response
	//	))completionBlock
	//
	//	{
	//
	//		NSString * extratext = paymentInfo.extra.extra_text;
	//		if(0 == extratext.length) {
	//			completionBlock(nil);
	//			return;
	//		}
	//
	//		NSDictionary * reqiredSignatureParams =
	//		@ {
	//		@ "merch_id":_config[ @ "OMNMailRu_merch_id"],
	//		@ "vterm_id":_config[ @ "OMNMailRu_vterm_id"],
	//		@ "user_login":paymentInfo.user_login,
	//		@ "order_id":paymentInfo.order_id,
	//		@ "order_amount":paymentInfo.order_amount,
	//		@ "order_message":paymentInfo.order_message,
	//		@ "extra":extratext,
	//	} ;
	//
	//		NSString * signature =[reqiredSignatureParams omn_signature];
	//
	//		NSMutableDictionary * parameters =[reqiredSignatureParams mutableCopy];
	//
	//		parameters[ @ "signature"]=signature;
	//		NSDictionary * card_info =[paymentInfo.cardInfo card_info];
	//		[parameters addEntriesFromDictionary:card_info];
	//
	//		parameters[ @ "cardholder"]=_config[ @ "OMNMailRu_cardholder"];
	//		parameters[ @ "user_phone"]=paymentInfo.user_phone;
	//
	//		__weak typeof (self) weakSelf = self;
	//		[self POST:@ "order/pay" parameters:
	//	parameters success:^(AFHTTPRequestOperation * operation, id responseObject){
	//
	//		if(responseObject[ @ "url"]&&
	//		nil == responseObject[ @ "error"]){
	//
	//			[weakSelf pollUrl:responseObject[ @ "url"]withCompletion:
	//			completionBlock];
	//
	//		}
	//		else{
	//
	//			completionBlock(responseObject);
	//
	//		}
	//
	//	} failure:^(AFHTTPRequestOperation * operation, NSError * error){
	//
	//		completionBlock([operation omn_errorResponse]);
	//
	//	}];
	//
	//	}
	//
	//	-(void)cardDelete:(NSString*)
	//	card_id user_login
	//	:(NSString*)
	//	user_login completion
	//	:(void(^)(
	//	id response
	//	))completionBlock
	//
	//	{
	//
	//		NSDictionary * reqiredSignatureParams =
	//		@ {
	//		@ "merch_id":_config[ @ "OMNMailRu_merch_id"],
	//		@ "vterm_id":_config[ @ "OMNMailRu_vterm_id"],
	//		@ "user_login":user_login,
	//		@ "card_id":card_id,
	//	} ;
	//
	//		NSMutableDictionary * parameters =[reqiredSignatureParams mutableCopy];
	//		parameters[ @ "signature"]=[reqiredSignatureParams omn_signature];
	//
	//		[self POST:@ "card/delete" parameters:
	//	parameters success:^(AFHTTPRequestOperation * operation, id responseObject){
	//
	//		completionBlock(responseObject);
	//
	//	}
	//		failure:^(AFHTTPRequestOperation * operation, NSError * error){
	//
	//		completionBlock([operation omn_errorResponse]);
	//
	//	}];
	//
	//	}

	public AcquiringMailRu(final Context context) {
		mContext = context;

		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		final GsonConverter converter = new GsonConverter(gson);

		RestAdapter mRestAdapter = new RestAdapter.Builder().setEndpoint(context.getString(R.string.acquiring_mailru_acquiring_base_url))
		                                                    .setLogLevel(
				                                                    logLevel)
		                                                    .setConverter(converter).build();
		mAcquiringService = mRestAdapter.create(AcquiringServiceMailRu.class);
	}

	@Override
	public void registerCard(RegisterCardRequest request) {
		HashMap<String, String> reqiredSignatureParams = new HashMap<String, String>();
		reqiredSignatureParams.put("merch_id", request.getMerch_id());
		reqiredSignatureParams.put("vterm_id", request.getVterm_id());
		reqiredSignatureParams.put("user_login", request.getUser_login());
		final String signature = getSignature(reqiredSignatureParams);
		request.setSignature(signature);

		final HashMap<String, String> parameters = reqiredSignatureParams;

		parameters.put("user_phone", request.getUser_phone());
		parameters.put("cardholder", mContext.getString(R.string.acquiring_mailru_cardholder));
		parameters.put("pan", request.getPan());
		parameters.put("cvv", request.getCvv());
		parameters.put("exp_date", request.getExp_date());
		parameters.put("signature", signature);

		mAcquiringService.registerCard(parameters)
//		                 .map(new Func1<RegisterCardResponse, Pair<String, String>>() {
//			                 @Override
//			                 public Pair<String, String> call(RegisterCardResponse acquiringResponse) {
//				                 return Pair.create(acquiringResponse.getCardId(), acquiringResponse.getUrl());
//			                 }
//		                 })
		                 .concatMap(new Func1<RegisterCardResponse, Observable<CardRegisterPollingResponse>>() {
			                 @Override
			                 public Observable<CardRegisterPollingResponse> call(RegisterCardResponse response) {
				                 return new PollingObservable(response);
			                 }
		                 })
		                 .subscribe(new Action1<CardRegisterPollingResponse>() {
			                 @Override
			                 public void call(CardRegisterPollingResponse response) {
				                 final String status = response.getStatus();
				                 final String cardId = response.getCardId();
				                 System.err.println("status = " + status + " cardId = " + cardId);
			                 }
		                 }, new Action1<Throwable>() {
			                 @Override
			                 public void call(Throwable throwable) {
				                 Log.e(TAG, "registerCard", throwable);
			                 }
		                 });

		// TODO: Call REST
		//		__weak typeof (self) weakSelf = self;
		//		[self POST:@ "card/register" parameters:
		//		parameters success:^(AFHTTPRequestOperation * operation, id responseObject){
		//
		//			if(responseObject[ @ "url"]){
		//
		//				[weakSelf checkRegisterForResponse:responseObject withCompletion:completionBlock];
		//
		//			}
		//			else{
		//
		//				completionBlock(responseObject, nil);
		//
		//			}
		//
		//		} failure:^(AFHTTPRequestOperation * operation, NSError * error){
		//
		//			completionBlock([operation omn_errorResponse],nil);
		//
		//		}];
	}

	@DebugLog
	private String getSignature(final HashMap<String, String> params) {
		final TreeSet<String> keys = new TreeSet<String>(params.keySet());
		final StringBuilder builder = new StringBuilder();
		System.err.println(">>> " + keys);
		for(final String key : keys) {
			builder.append(params.get(key));
		}
		final String s = builder.toString();
		System.err.println(">>> " + s);
		System.err.println(">>> " + s + mContext.getString(R.string.acquiring_mailru_secret_key));
		return encryptPassword(s + mContext.getString(R.string.acquiring_mailru_secret_key));
	}
}
