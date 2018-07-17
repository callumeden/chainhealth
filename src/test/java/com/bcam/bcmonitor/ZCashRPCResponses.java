package com.bcam.bcmonitor;

public class ZCashRPCResponses {

    public static final String getTransactionResponse = "{\n" +
            "    \"result\": {\n" +
            "        \"hex\": \"01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff025100ffffffff0250c30000000000002321027a46eb513588b01b37ea24303f4b628afd12cc20df789fede0921e43cad3e875acd43000000000000017a9147d46a730d31f97b1930d3368a967c309bd4d136a8700000000\",\n" +
            "        \"txid\": \"851bf6fbf7a976327817c738c489d7fa657752445430922d94c983c0b9ed4609\",\n" +
            "        \"overwintered\": false,\n" +
            "        \"version\": 1,\n" +
            "        \"locktime\": 0,\n" +
            "        \"vin\": [{\n" +
            "            \"coinbase\": \"5100\",\n" +
            "            \"sequence\": 4294967295\n" +
            "        }],\n" +
            "        \"vout\": [{\n" +
            "            \"value\": 0.00050000,\n" +
            "            \"valueZat\": 50000,\n" +
            "            \"n\": 0,\n" +
            "            \"scriptPubKey\": {\n" +
            "                \"asm\": \"027a46eb513588b01b37ea24303f4b628afd12cc20df789fede0921e43cad3e875 OP_CHECKSIG\",\n" +
            "                \"hex\": \"21027a46eb513588b01b37ea24303f4b628afd12cc20df789fede0921e43cad3e875ac\",\n" +
            "                \"reqSigs\": 1,\n" +
            "                \"type\": \"pubkey\",\n" +
            "                \"addresses\": [\"t1KstPVzcNEK4ZeauQ6cogoqxQBMDSiRnGr\"]\n" +
            "            }\n" +
            "        }, {\n" +
            "            \"value\": 0.00012500,\n" +
            "            \"valueZat\": 12500,\n" +
            "            \"n\": 1,\n" +
            "            \"scriptPubKey\": {\n" +
            "                \"asm\": \"OP_HASH160 7d46a730d31f97b1930d3368a967c309bd4d136a OP_EQUAL\",\n" +
            "                \"hex\": \"a9147d46a730d31f97b1930d3368a967c309bd4d136a87\",\n" +
            "                \"reqSigs\": 1,\n" +
            "                \"type\": \"scripthash\",\n" +
            "                \"addresses\": [\"t3Vz22vK5z2LcKEdg16Yv4FFneEL1zg9ojd\"]\n" +
            "            }\n" +
            "        }],\n" +
            "        \"vjoinsplit\": [],\n" +
            "        \"blockhash\": \"0007bc227e1c57a4a70e237cad00e7b7ce565155ab49166bc57397a26d339283\",\n" +
            "        \"confirmations\": 4653,\n" +
            "        \"time\": 1477671596,\n" +
            "        \"blocktime\": 1477671596\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"curltest\"\n" +
            "}";


    public static final String getBlockResponse = "{\n" +
            "    \"result\": {\n" +
            "        \"hash\": \"0007bc227e1c57a4a70e237cad00e7b7ce565155ab49166bc57397a26d339283\",\n" +
            "        \"confirmations\": 5248,\n" +
            "        \"size\": 1617,\n" +
            "        \"height\": 1,\n" +
            "        \"version\": 4,\n" +
            "        \"merkleroot\": \"851bf6fbf7a976327817c738c489d7fa657752445430922d94c983c0b9ed4609\",\n" +
            "        \"finalsaplingroot\": \"0000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "        \"tx\": [\"851bf6fbf7a976327817c738c489d7fa657752445430922d94c983c0b9ed4609\"],\n" +
            "        \"time\": 1477671596,\n" +
            "        \"nonce\": \"9057977ea6d4ae867decc96359fcf2db8cdebcbfb3bd549de4f21f16cfe83475\",\n" +
            "        \"solution\": \"002b2ee0d2f5d0c1ebf5a265b6f5b428f2fdc9aaea07078a6c5cab4f1bbfcd56489863deae6ea3fd8d3d0762e8e5295ff2670c9e90d8e8c68a54a40927e82a65e1d44ced20d835818e172d7b7f5ffe0245d0c3860a3f11af5658d68b6a7253b4684ffef5242fefa77a0bfc3437e8d94df9dc57510f5a128e676dd9ddf23f0ef75b460090f507499585541ab53a470c547ea02723d3a979930941157792c4362e42d3b9faca342a5c05a56909b046b5e92e2870fca7c932ae2c2fdd97d75b6e0ecb501701c1250246093c73efc5ec2838aeb80b59577741aa5ccdf4a631b79f70fc419e28714fa22108d991c29052b2f5f72294c355b57504369313470ecdd8e0ae97fc48e243a38c2ee7315bb05b7de9602047e97449c81e46746513221738dc729d7077a1771cea858865d85261e71e82003ccfbba2416358f023251206d6ef4c5596bc35b2b5bce3e9351798aa2c9904723034e5815c7512d260cc957df5db6adf9ed7272483312d1e68c60955a944e713355089876a704aef06359238f6de5a618f7bd0b4552ba72d05a6165e582f62d55ff2e1b76991971689ba3bee16a520fd85380a6e5a31de4dd4654d561101ce0ca390862d5774921eae2c284008692e9e08562144e8aa1f399a9d3fab0c4559c1f12bc945e626f7a89668613e8829767f4116ee9a4f832cf7c3ade3a7aba8cb04de39edd94d0d05093ed642adf9fbd9d373a80832ffd1c62034e4341546b3515f0e42e6d8570393c6754be5cdb7753b4709527d3f164aebf3d315934f7b3736a1b31052f6cc5699758950331163b3df05b9772e9bf99c8c77f8960e10a15edb06200106f45742d740c422c86b7e4f5a52d3732aa79ee54cfc92f76e03c268ae226477c19924e733caf95b8f350233a5312f4ed349d3ad76f032358f83a6d0d6f83b2a456742aad7f3e615fa72286300f0ea1c9793831ef3a5a4ae08640a6e32f53d1cba0be284b25e923d0d110ba227e54725632efcbbe17c05a9cde976504f6aece0c461b562cfae1b85d5f6782ee27b3e332ac0775f681682ce524b32889f1dc4231226f1aada0703beaf8d41732c9647a0a940a86f8a1be7f239c44fcaa7ed7a055506bdbe1df848f9e047226bee1b6d788a03f6e352eead99b419cfc41741942dbeb7a5c55788d5a3e636d8aab7b36b4db71d16700373bbc1cdeba8f9b1db10bf39a621bc737ea4f4e333698d6e09b51ac7a97fb6fd117ccad1d6b6b3a7451699d5bfe448650396d7b58867b3b0872be13ad0b43da267df0ad77025155f04e20c56d6a9befb3e9c7d23b82cbf3a534295ebda540682cc81be9273781b92519c858f9c25294fbacf75c3b3c15bda6d36de1c83336f93e96910dbdcb190d6ef123c98565ff6df1e903f57d4e4df167ba6b829d6d9713eb2126b0cf869940204137babcc6a1b7cb2f0b94318a7460e5d1a605c249bd2e72123ebad332332c18adcb285ed8874dbde084ebcd4f744465350d57110f037fffed1569d642c258749e65b0d13e117eaa37014a769b5ab479b7c77178880e77099f999abe712e543dbbf626ca9bcfddc42ff2f109d21c8bd464894e55ae504fdf81e1a7694180225da7dac8879abd1036cf26bb50532b8cf138b337a1a1bd1a43f8dd70b7399e2690c8e7a5a1fe099026b8f2a6f65fc0dbedda15ba65e0abd66c7176fb426980549892b4817de78e345a7aeab05744c3def4a2f283b4255b02c91c1af7354a368c67a11703c642a385c7453131ce3a78b24c5e22ab7e136a38498ce82082181884418cb4d6c2920f258a3ad20cfbe7104af1c6c6cb5e58bf29a9901721ad19c0a260cd09a3a772443a45aea4a5c439a95834ef5dc2e26343278947b7b796f796ae9bcadb29e2899a1d7313e6f7bfb6f8b\",\n" +
            "        \"bits\": \"1f07ffff\",\n" +
            "        \"difficulty\": 1,\n" +
            "        \"chainwork\": \"0000000000000000000000000000000000000000000000000000000000004000\",\n" +
            "        \"anchor\": \"59d2cde5e65c1414c32ba54f0fe4bdb3d67618125286e6a191317917c812c6d7\",\n" +
            "        \"valuePools\": [{\n" +
            "            \"id\": \"sprout\",\n" +
            "            \"monitored\": true,\n" +
            "            \"chainValue\": 0.00000000,\n" +
            "            \"chainValueZat\": 0,\n" +
            "            \"valueDelta\": 0.00000000,\n" +
            "            \"valueDeltaZat\": 0\n" +
            "        }, {\n" +
            "            \"id\": \"sapling\",\n" +
            "            \"monitored\": true,\n" +
            "            \"chainValue\": 0.00000000,\n" +
            "            \"chainValueZat\": 0,\n" +
            "            \"valueDelta\": 0.00000000,\n" +
            "            \"valueDeltaZat\": 0\n" +
            "        }],\n" +
            "        \"previousblockhash\": \"00040fe8ec8471911baa1db1266ea15dd06b4a8a5c453883c000b031973dce08\",\n" +
            "        \"nextblockhash\": \"0002a26c902619fc964443264feb16f1e3e2d71322fc53dcb81cc5d797e273ed\"\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"curltest\"\n" +
            "}";


    public static final String getBlockchainInfoResponse = "{\n" +
            "    \"result\": {\n" +
            "        \"chain\": \"main\",\n" +
            "        \"blocks\": 5248,\n" +
            "        \"headers\": 52959,\n" +
            "        \"bestblockhash\": \"00000000b0e31a8bb7bbcf902e7f854599e369984b9f85f2d2f1e3cfd9c0f265\",\n" +
            "        \"difficulty\": 183195.8968846804,\n" +
            "        \"verificationprogress\": 0.007334830995587324,\n" +
            "        \"chainwork\": \"000000000000000000000000000000000000000000000000000002e6a5d08190\",\n" +
            "        \"pruned\": false,\n" +
            "        \"commitments\": 22252,\n" +
            "        \"valuePools\": [{\n" +
            "            \"id\": \"sprout\",\n" +
            "            \"monitored\": true,\n" +
            "            \"chainValue\": 251.96536192,\n" +
            "            \"chainValueZat\": 25196536192\n" +
            "        }, {\n" +
            "            \"id\": \"sapling\",\n" +
            "            \"monitored\": true,\n" +
            "            \"chainValue\": 0.00000000,\n" +
            "            \"chainValueZat\": 0\n" +
            "        }],\n" +
            "        \"softforks\": [{\n" +
            "            \"id\": \"bip34\",\n" +
            "            \"version\": 2,\n" +
            "            \"enforce\": {\n" +
            "                \"status\": true,\n" +
            "                \"found\": 4000,\n" +
            "                \"required\": 750,\n" +
            "                \"window\": 4000\n" +
            "            },\n" +
            "            \"reject\": {\n" +
            "                \"status\": true,\n" +
            "                \"found\": 4000,\n" +
            "                \"required\": 950,\n" +
            "                \"window\": 4000\n" +
            "            }\n" +
            "        }, {\n" +
            "            \"id\": \"bip66\",\n" +
            "            \"version\": 3,\n" +
            "            \"enforce\": {\n" +
            "                \"status\": true,\n" +
            "                \"found\": 4000,\n" +
            "                \"required\": 750,\n" +
            "                \"window\": 4000\n" +
            "            },\n" +
            "            \"reject\": {\n" +
            "                \"status\": true,\n" +
            "                \"found\": 4000,\n" +
            "                \"required\": 950,\n" +
            "                \"window\": 4000\n" +
            "            }\n" +
            "        }, {\n" +
            "            \"id\": \"bip65\",\n" +
            "            \"version\": 4,\n" +
            "            \"enforce\": {\n" +
            "                \"status\": true,\n" +
            "                \"found\": 4000,\n" +
            "                \"required\": 750,\n" +
            "                \"window\": 4000\n" +
            "            },\n" +
            "            \"reject\": {\n" +
            "                \"status\": true,\n" +
            "                \"found\": 4000,\n" +
            "                \"required\": 950,\n" +
            "                \"window\": 4000\n" +
            "            }\n" +
            "        }],\n" +
            "        \"upgrades\": {\n" +
            "            \"5ba81b19\": {\n" +
            "                \"name\": \"Overwinter\",\n" +
            "                \"activationheight\": 347500,\n" +
            "                \"status\": \"pending\",\n" +
            "                \"info\": \"See https://z.cash/upgrade/overwinter.html for details.\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"consensus\": {\n" +
            "            \"chaintip\": \"00000000\",\n" +
            "            \"nextblock\": \"00000000\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"curltest\"\n" +
            "}";


    public static final String getBestBlockHashResponse = "{\n" +
            "    \"result\": \"00000000b0e31a8bb7bbcf902e7f854599e369984b9f85f2d2f1e3cfd9c0f265\",\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"curltest\"\n" +
            "}";


    public static final String getBlockHashResppnse = "{\n" +
            "    \"result\": \"0007bc227e1c57a4a70e237cad00e7b7ce565155ab49166bc57397a26d339283\",\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"curltest\"\n" +
            "}";


    public static final String getMempoolInfoResponse = "{\n" +
            "    \"result\": {\n" +
            "        \"size\": 0,\n" +
            "        \"bytes\": 0,\n" +
            "        \"usage\": 0\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"curltest\"\n" +
            "}";

    public static final String getMempoolResponse = "";

}