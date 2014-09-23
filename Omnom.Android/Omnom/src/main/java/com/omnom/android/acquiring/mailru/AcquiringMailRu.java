package com.omnom.android.acquiring.mailru;

import android.content.Context;

import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringMailRu implements Acquiring {

	private Context mContext;

	public AcquiringMailRu(final Context context) {
		mContext = context;
	}

	@Override
	public void registerCard(HashMap<String, String> cardInfo, String user_login, String user_phone, RegisterCardCallback callback) {
		HashMap<String, String> reqiredSignatureParams = new HashMap<String, String>();
		reqiredSignatureParams.put("merch_id", mContext.getString(R.string.acquiring_mailru_merch_id));
		reqiredSignatureParams.put("vterm_id", mContext.getString(R.string.acquiring_mailru_vterm_id));
		reqiredSignatureParams.put("user_login", user_login);

		HashMap<String, String> parameters = reqiredSignatureParams;

		parameters.put("signature", getSignature(reqiredSignatureParams));
		parameters.put("cardholder", "OMNMailRu_cardholder");
		parameters.put("user_phone", user_phone);

		parameters.putAll(cardInfo);

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

	private String getSignature(final HashMap<String, String> params) {
		final TreeSet<String> keys = new TreeSet<String>(params.keySet());
		final StringBuilder builder = new StringBuilder();
		for(final String key : keys) {
			builder.append(params.get(key));
		}
		return builder.toString();
	}
}
