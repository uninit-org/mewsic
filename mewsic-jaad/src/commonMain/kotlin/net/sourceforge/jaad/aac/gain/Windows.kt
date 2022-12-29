package net.sourceforge.jaad.aac.gain

//sine and kbd windows
internal interface Windows {
    companion object {
        val SINE_256 = floatArrayOf(
            0.003067956762965976f,
            0.00920375478205982f,
            0.0153392062849881f,
            0.021474080275469508f,
            0.02760814577896574f,
            0.03374117185137758f,
            0.03987292758773981f,
            0.04600318213091462f,
            0.052131704680283324f,
            0.05825826450043575f,
            0.06438263092985747f,
            0.07050457338961386f,
            0.07662386139203149f,
            0.08274026454937569f,
            0.0888535525825246f,
            0.09496349532963899f,
            0.10106986275482782f,
            0.10717242495680884f,
            0.11327095217756435f,
            0.11936521481099135f,
            0.12545498341154623f,
            0.13154002870288312f,
            0.13762012158648604f,
            0.14369503315029447f,
            0.1497645346773215f,
            0.15582839765426523f,
            0.16188639378011183f,
            0.16793829497473117f,
            0.17398387338746382f,
            0.18002290140569951f,
            0.18605515166344663f,
            0.19208039704989244f,
            0.19809841071795356f,
            0.20410896609281687f,
            0.2101118368804696f,
            0.21610679707621952f,
            0.2220936209732035f,
            0.22807208317088573f,
            0.23404195858354343f,
            0.2400030224487415f,
            0.2459550503357946f,
            0.25189781815421697f,
            0.257831102162159f,
            0.26375467897483135f,
            0.2696683255729151f,
            0.27557181931095814f,
            0.28146493792575794f,
            0.2873474595447295f,
            0.29321916269425863f,
            0.2990798263080405f,
            0.3049292297354024f,
            0.3107671527496115f,
            0.31659337555616585f,
            0.32240767880106985f,
            0.3282098435790925f,
            0.3339996514420094f,
            0.33977688440682685f,
            0.3455413249639891f,
            0.3512927560855671f,
            0.35703096123343f,
            0.3627557243673972f,
            0.3684668299533723f,
            0.37416406297145793f,
            0.37984720892405116f,
            0.38551605384391885f,
            0.39117038430225387f,
            0.3968099874167103f,
            0.40243465085941843f,
            0.4080441628649787f,
            0.4136383122384345f,
            0.4192168883632239f,
            0.4247796812091088f,
            0.4303264813400826f,
            0.4358570799222555f,
            0.44137126873171667f,
            0.44686884016237416f,
            0.4523495872337709f,
            0.4578133035988772f,
            0.46325978355186015f,
            0.4686888220358279f,
            0.47410021465054997f,
            0.479493757660153f,
            0.48486924800079106f,
            0.49022648328829116f,
            0.49556526182577254f,
            0.5008853826112407f,
            0.5061866453451552f,
            0.5114688504379703f,
            0.5167317990176499f,
            0.5219752929371544f,
            0.5271991347819013f,
            0.5324031278771979f,
            0.5375870762956454f,
            0.5427507848645159f,
            0.5478940591731002f,
            0.5530167055800275f,
            0.5581185312205561f,
            0.5631993440138341f,
            0.5682589526701315f,
            0.5732971666980422f,
            0.5783137964116556f,
            0.5833086529376983f,
            0.5882815482226452f,
            0.5932322950397998f,
            0.5981607069963423f,
            0.6030665985403482f,
            0.6079497849677736f,
            0.6128100824294097f,
            0.6176473079378039f,
            0.62246127937415f,
            0.6272518154951441f,
            0.6320187359398091f,
            0.6367618612362842f,
            0.6414810128085832f,
            0.6461760129833163f,
            0.6508466849963809f,
            0.6554928529996153f,
            0.6601143420674205f,
            0.6647109782033448f,
            0.669282588346636f,
            0.673829000378756f,
            0.6783500431298615f,
            0.6828455463852481f,
            0.687315340891759f,
            0.6917592583641577f,
            0.696177131491463f,
            0.7005687939432483f,
            0.7049340803759049f,
            0.7092728264388657f,
            0.7135848687807935f,
            0.7178700450557317f,
            0.7221281939292153f,
            0.726359155084346f,
            0.7305627692278276f,
            0.7347388780959634f,
            0.7388873244606151f,
            0.7430079521351217f,
            0.7471006059801801f,
            0.7511651319096864f,
            0.7552013768965365f,
            0.759209188978388f,
            0.7631884172633813f,
            0.7671389119358204f,
            0.7710605242618138f,
            0.7749531065948738f,
            0.7788165123814759f,
            0.7826505961665757f,
            0.7864552135990858f,
            0.79023022143731f,
            0.7939754775543372f,
            0.797690840943391f,
            0.8013761717231401f,
            0.8050313311429635f,
            0.808656181588175f,
            0.8122505865852039f,
            0.8158144108067338f,
            0.8193475200767969f,
            0.8228497813758263f,
            0.8263210628456635f,
            0.829761233794523f,
            0.8331701647019132f,
            0.8365477272235119f,
            0.8398937941959994f,
            0.8432082396418454f,
            0.8464909387740521f,
            0.8497417680008524f,
            0.8529606049303636f,
            0.8561473283751945f,
            0.8593018183570084f,
            0.8624239561110405f,
            0.865513624090569f,
            0.8685707059713409f,
            0.8715950866559511f,
            0.8745866522781761f,
            0.8775452902072612f,
            0.8804708890521608f,
            0.8833633386657316f,
            0.8862225301488806f,
            0.8890483558546646f,
            0.8918407093923427f,
            0.8945994856313826f,
            0.8973245807054183f,
            0.9000158920161603f,
            0.9026733182372588f,
            0.9052967593181188f,
            0.9078861164876663f,
            0.9104412922580671f,
            0.9129621904283981f,
            0.9154487160882678f,
            0.9179007756213904f,
            0.9203182767091105f,
            0.9227011283338785f,
            0.9250492407826776f,
            0.9273625256504011f,
            0.9296408958431812f,
            0.9318842655816681f,
            0.9340925504042589f,
            0.9362656671702783f,
            0.9384035340631081f,
            0.9405060705932683f,
            0.9425731976014469f,
            0.9446048372614803f,
            0.9466009130832835f,
            0.9485613499157303f,
            0.9504860739494817f,
            0.9523750127197659f,
            0.9542280951091057f,
            0.9560452513499964f,
            0.9578264130275329f,
            0.9595715130819845f,
            0.9612804858113206f,
            0.9629532668736839f,
            0.9645897932898126f,
            0.9661900034454126f,
            0.9677538370934755f,
            0.9692812353565485f,
            0.9707721407289504f,
            0.9722264970789363f,
            0.9736442496508119f,
            0.9750253450669941f,
            0.9763697313300211f,
            0.9776773578245099f,
            0.9789481753190622f,
            0.9801821359681173f,
            0.9813791933137546f,
            0.9825393022874412f,
            0.9836624192117303f,
            0.9847485018019042f,
            0.9857975091675674f,
            0.9868094018141854f,
            0.9877841416445722f,
            0.9887216919603238f,
            0.9896220174632008f,
            0.990485084256457f,
            0.9913108598461154f,
            0.9920993131421918f,
            0.9928504144598651f,
            0.9935641355205953f,
            0.9942404494531879f,
            0.9948793307948056f,
            0.9954807554919269f,
            0.996044700901252f,
            0.9965711457905548f,
            0.997060070339483f,
            0.9975114561403035f,
            0.997925286198596f,
            0.9983015449338929f,
            0.9986402181802653f,
            0.9989412931868569f,
            0.9992047586183639f,
            0.9994306045554617f,
            0.9996188224951786f,
            0.9997694053512153f,
            0.9998823474542126f,
            0.9999576445519639f,
            0.9999952938095762f
        )
        val SINE_32 = floatArrayOf(
            0.024541228522912288f,
            0.07356456359966743f,
            0.1224106751992162f,
            0.17096188876030122f,
            0.2191012401568698f,
            0.26671275747489837f,
            0.3136817403988915f,
            0.3598950365349881f,
            0.40524131400498986f,
            0.44961132965460654f,
            0.49289819222978404f,
            0.5349976198870972f,
            0.5758081914178453f,
            0.6152315905806268f,
            0.6531728429537768f,
            0.6895405447370668f,
            0.7242470829514669f,
            0.7572088465064846f,
            0.7883464276266062f,
            0.8175848131515837f,
            0.844853565249707f,
            0.8700869911087113f,
            0.8932243011955153f,
            0.9142097557035307f,
            0.9329927988347388f,
            0.9495281805930367f,
            0.9637760657954398f,
            0.9757021300385286f,
            0.9852776423889412f,
            0.99247953459871f,
            0.9972904566786902f,
            0.9996988186962042f
        )
        val KBD_256 = floatArrayOf(
            0.0005851230124487f,
            0.0009642149851497f,
            0.0013558207534965f,
            0.0017771849644394f,
            0.0022352533849672f,
            0.0027342299070304f,
            0.0032773001022195f,
            0.0038671998069216f,
            0.0045064443384152f,
            0.0051974336885144f,
            0.0059425050016407f,
            0.0067439602523141f,
            0.0076040812644888f,
            0.0085251378135895f,
            0.0095093917383048f,
            0.0105590986429280f,
            0.0116765080854300f,
            0.0128638627792770f,
            0.0141233971318631f,
            0.0154573353235409f,
            0.0168678890600951f,
            0.0183572550877256f,
            0.0199276125319803f,
            0.0215811201042484f,
            0.0233199132076965f,
            0.0251461009666641f,
            0.0270617631981826f,
            0.0290689473405856f,
            0.0311696653515848f,
            0.0333658905863535f,
            0.0356595546648444f,
            0.0380525443366107f,
            0.0405466983507029f,
            0.0431438043376910f,
            0.0458455957104702f,
            0.0486537485902075f,
            0.0515698787635492f,
            0.0545955386770205f,
            0.0577322144743916f,
            0.0609813230826460f,
            0.0643442093520723f,
            0.0678221432558827f,
            0.0714163171546603f,
            0.0751278431308314f,
            0.0789577503982528f,
            0.0829069827918993f,
            0.0869763963425241f,
            0.0911667569410503f,
            0.0954787380973307f,
            0.0999129187977865f,
            0.1044697814663005f,
            0.1091497100326053f,
            0.1139529881122542f,
            0.1188797973021148f,
            0.1239302155951605f,
            0.1291042159181728f,
            0.1344016647957880f,
            0.1398223211441467f,
            0.1453658351972151f,
            0.1510317475686540f,
            0.1568194884519144f,
            0.1627283769610327f,
            0.1687576206143887f,
            0.1749063149634756f,
            0.1811734433685097f,
            0.1875578769224857f,
            0.1940583745250518f,
            0.2006735831073503f,
            0.2074020380087318f,
            0.2142421635060113f,
            0.2211922734956977f,
            0.2282505723293797f,
            0.2354151558022098f,
            0.2426840122941792f,
            0.2500550240636293f,
            0.2575259686921987f,
            0.2650945206801527f,
            0.2727582531907993f,
            0.2805146399424422f,
            0.2883610572460804f,
            0.2962947861868143f,
            0.3043130149466800f,
            0.3124128412663888f,
            0.3205912750432127f,
            0.3288452410620226f,
            0.3371715818562547f,
            0.3455670606953511f,
            0.3540283646950029f,
            0.3625521080463003f,
            0.3711348353596863f,
            0.3797730251194006f,
            0.3884630932439016f,
            0.3972013967475546f,
            0.4059842374986933f,
            0.4148078660689724f,
            0.4236684856687616f,
            0.4325622561631607f,
            0.4414852981630577f,
            0.4504336971855032f,
            0.4594035078775303f,
            0.4683907582974173f,
            0.4773914542472655f,
            0.4864015836506502f,
            0.4954171209689973f,
            0.5044340316502417f,
            0.5134482766032377f,
            0.5224558166913167f,
            0.5314526172383208f,
            0.5404346525403849f,
            0.5493979103766972f,
            0.5583383965124314f,
            0.5672521391870222f,
            0.5761351935809411f,
            0.5849836462541291f,
            0.5937936195492526f,
            0.6025612759529649f,
            0.6112828224083939f,
            0.6199545145721097f,
            0.6285726610088878f,
            0.6371336273176413f,
            0.6456338401819751f,
            0.6540697913388968f,
            0.6624380414593221f,
            0.6707352239341151f,
            0.6789580485595255f,
            0.6871033051160131f,
            0.6951678668345944f,
            0.7031486937449871f,
            0.7110428359000029f,
            0.7188474364707993f,
            0.7265597347077880f,
            0.7341770687621900f,
            0.7416968783634273f,
            0.7491167073477523f,
            0.7564342060337386f,
            0.7636471334404891f,
            0.7707533593446514f,
            0.7777508661725849f,
            0.7846377507242818f,
            0.7914122257259034f,
            0.7980726212080798f,
            0.8046173857073919f,
            0.8110450872887550f,
            0.8173544143867162f,
            0.8235441764639875f,
            0.8296133044858474f,
            0.8355608512093652f,
            0.8413859912867303f,
            0.8470880211822968f,
            0.8526663589032990f,
            0.8581205435445334f,
            0.8634502346476508f,
            0.8686552113760616f,
            0.8737353715068081f,
            0.8786907302411250f,
            0.8835214188357692f,
            0.8882276830575707f,
            0.8928098814640207f,
            0.8972684835130879f,
            0.9016040675058185f,
            0.9058173183656508f,
            0.9099090252587376f,
            0.9138800790599416f,
            0.9177314696695282f,
            0.9214642831859411f,
            0.9250796989403991f,
            0.9285789863994010f,
            0.9319635019415643f,
            0.9352346855155568f,
            0.9383940571861993f,
            0.9414432135761304f,
            0.9443838242107182f,
            0.9472176277741918f,
            0.9499464282852282f,
            0.9525720912004834f,
            0.9550965394547873f,
            0.9575217494469370f,
            0.9598497469802043f,
            0.9620826031668507f,
            0.9642224303060783f,
            0.9662713777449607f,
            0.9682316277319895f,
            0.9701053912729269f,
            0.9718949039986892f,
            0.9736024220549734f,
            0.9752302180233160f,
            0.9767805768831932f,
            0.9782557920246753f,
            0.9796581613210076f,
            0.9809899832703159f,
            0.9822535532154261f,
            0.9834511596505429f,
            0.9845850806232530f,
            0.9856575802399989f,
            0.9866709052828243f,
            0.9876272819448033f,
            0.9885289126911557f,
            0.9893779732525968f,
            0.9901766097569984f,
            0.9909269360049311f,
            0.9916310308941294f,
            0.9922909359973702f,
            0.9929086532976777f,
            0.9934861430841844f,
            0.9940253220113651f,
            0.9945280613237534f,
            0.9949961852476154f,
            0.9954314695504363f,
            0.9958356402684387f,
            0.9962103726017252f,
            0.9965572899760172f,
            0.9968779632693499f,
            0.9971739102014799f,
            0.9974465948831872f,
            0.9976974275220812f,
            0.9979277642809907f,
            0.9981389072844972f,
            0.9983321047686901f,
            0.9985085513687731f,
            0.9986693885387259f,
            0.9988157050968516f,
            0.9989485378906924f,
            0.9990688725744943f,
            0.9991776444921379f,
            0.9992757396582338f,
            0.9993639958299003f,
            0.9994432036616085f,
            0.9995141079353859f,
            0.9995774088586188f,
            0.9996337634216871f,
            0.9996837868076957f,
            0.9997280538466377f,
            0.9997671005064359f,
            0.9998014254134544f,
            0.9998314913952471f,
            0.9998577270385304f,
            0.9998805282555989f,
            0.9999002598526793f,
            0.9999172570940037f,
            0.9999318272557038f,
            0.9999442511639580f,
            0.9999547847121726f,
            0.9999636603523446f,
            0.9999710885561258f,
            0.9999772592414866f,
            0.9999823431612708f,
            0.9999864932503106f,
            0.9999898459281599f,
            0.9999925223548691f,
            0.9999946296375997f,
            0.9999962619864214f,
            0.9999975018180320f,
            0.9999984208055542f,
            0.9999990808746198f,
            0.9999995351446231f,
            0.9999998288155155f
        )
        val KBD_32 = floatArrayOf(
            0.0000875914060105f,
            0.0009321760265333f,
            0.0032114611466596f,
            0.0081009893216786f,
            0.0171240286619181f,
            0.0320720743527833f,
            0.0548307856028528f,
            0.0871361822564870f,
            0.1302923415174603f,
            0.1848955425508276f,
            0.2506163195331889f,
            0.3260874142923209f,
            0.4089316830907141f,
            0.4959414909423747f,
            0.5833939894958904f,
            0.6674601983218376f,
            0.7446454751465113f,
            0.8121892962974020f,
            0.8683559394406505f,
            0.9125649996381605f,
            0.9453396205809574f,
            0.9680864942677585f,
            0.9827581789763112f,
            0.9914756203467121f,
            0.9961964092194694f,
            0.9984956609571091f,
            0.9994855586984285f,
            0.9998533730714648f,
            0.9999671864476404f,
            0.9999948432453556f,
            0.9999995655238333f,
            0.9999999961638728f
        )
    }
}
